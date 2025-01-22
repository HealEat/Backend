package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.repository.SearchResultRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto.SearchInfo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.hibernate.internal.util.MutableInteger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreSearchService {

    private final StoreApiClient storeApiClient;
    private final SearchLocationService searchLocationService;
    private final SearchFeatureService searchFeatureService;
    private final StoreMappingService storeMappingService;
    private final StoreApiCallService apiCallService;

    private final FoodFeatureRepository foodFeatureRepository;
    private final SearchResultRepository searchResultRepository;

    @Transactional
    public SearchResult searchAndSave(
            StoreRequestDto.SearchKeywordDto request,
            StoreRequestDto.SearchFilterDto filter,
            MutableInteger apiCallCounter) {

        SearchResultKey key = createSearchResultKey(request);

        Optional<SearchResult> existingResult = searchResultRepository
                .findBySearchIdAndCreatedAtAfter(
                        key.generateId(),
                        LocalDateTime.now().minusMinutes(30)
                );

        if (existingResult.isPresent()) {
            apiCallCounter.set(0);
            return existingResult.get();
        }

        Pair<List<Document>, SearchInfo> searchResult = searchStores(request, filter);
        SearchResult result = saveSearchResult(key, searchResult);
        apiCallCounter.set(result.getApiCallCount());
        return result;
    }

    private SearchResultKey createSearchResultKey(StoreRequestDto.SearchKeywordDto request) {
        return SearchResultKey.builder()
                .query(request.getQuery())
                .x(request.getX())
                .y(request.getY())
                .build();
    }

    @Transactional
    public SearchResult saveSearchResult(SearchResultKey key, Pair<List<Document>, SearchInfo> searchResult) {
        SearchInfo searchInfo = searchResult.getSecond();
        searchInfo.setApiCallCount(apiCallService.getAndResetApiCallCount());

        SearchResult result = SearchResult.builder()
                .searchId(key.generateId())
                .baseX(searchInfo.getBaseX())
                .baseY(searchInfo.getBaseY())
                .query(searchInfo.getQuery())
                .selectedRegion(searchInfo.getSelectedRegion())
                .otherRegions(searchInfo.getOtherRegions())
                .apiCallCount(searchInfo.getApiCallCount())
                .build();

        searchResult.getFirst().forEach(document -> {

            Set<FoodFeature> features = searchFeatureService.extractFeaturesForDocument(document);

            SearchResultItem item = storeMappingService.mapDocumentToSearchResultItem(document, features);

            result.addItem(item);
        });

        return searchResultRepository.save(result);
    }

    /**
     * 메인 검색 로직
     */
    public Pair<List<Document>, SearchInfo> searchStores(
            StoreRequestDto.SearchKeywordDto request,
            StoreRequestDto.SearchFilterDto filter) {

        String query = request.getQuery();

        if (query == null || query.isBlank()) {
            return handleEmptyQuery(request);
        }

        InitialSearchResult initialResult = performInitialSearch(request);

        SearchFeatureService.FeatureExtractionResult featureResult = searchFeatureService
                .extractFeatures(initialResult.keyword());
        filter.getCategoryIdList().addAll(featureResult.getNewCategoryIds());

        return performFinalSearch(request, initialResult, featureResult, filter);
    }

    /**
     * 빈 쿼리 처리
     */
    private Pair<List<Document>, SearchInfo> handleEmptyQuery(StoreRequestDto.SearchKeywordDto request) {
        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query("")
                .addedFilterFromQuery("")
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        List<Document> documents = searchLocationService.getDocsOnLoopByLocation(request.getX(), request.getY());
        searchInfo.setApiCallCount(apiCallService.getAndResetApiCallCount());
        return Pair.of(documents, searchInfo);
    }

    /**
     * 초기 검색 결과를 담는 클래스
     */
    @Builder
    private record InitialSearchResult(KakaoPlaceResponseDto response,
                                       String selectedAddress,
                                       String keyword,
                                       List<Document> initialDocuments) {
    }

    /**
     * 초기 검색 수행
     */
    private InitialSearchResult performInitialSearch(StoreRequestDto.SearchKeywordDto request) {
        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                request.getQuery() + " 식당",
                request.getX(), request.getY(), 1, "accuracy", "FD6");
        apiCallService.incrementApiCallCount();

        return InitialSearchResult.builder()
                .response(kakaoList)
                .selectedAddress(kakaoList.getMeta().getSame_name().getSelected_region())
                .keyword(kakaoList.getMeta().getSame_name().getKeyword().replaceAll(" 식당", ""))
                .initialDocuments(new ArrayList<>(kakaoList.getDocuments()))
                .build();
    }

    /**
     * 최종 검색 수행
     */
    private Pair<List<Document>, SearchInfo> performFinalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            SearchFeatureService.FeatureExtractionResult featureResult,
            StoreRequestDto.SearchFilterDto filter) {

        /*
            "왕십리 소화가 잘되는 음식"
            -> 카카오 로컬 API에서 지역 인식을 하지 못함.
            따라서 handleNonRegionalSearch에서
            이와 같은 상황은 특수 케이스 분류로 처리
        */
        if (initialResult.selectedAddress() == null || initialResult.selectedAddress().isBlank()) {
            // "근처 맛집", "부드러운 음식", "왕십리 소화가 잘되는 음식"
            return handleNonRegionalSearch(request, initialResult, featureResult, filter);
        } else {
            // "아차산 맛집", "경대병원 죽", "홍대 소화가 잘되는 음식"
            return handleRegionalSearch(request, initialResult, featureResult);
        }
    }

    /**
     * 지역 없는 검색 처리
     */
    private Pair<List<Document>, SearchInfo> handleNonRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            SearchFeatureService.FeatureExtractionResult featureResult,
            StoreRequestDto.SearchFilterDto filter) {

        String noWhiteKeyword = featureResult.getProcessedKeyword().replaceAll("\\s+", "");
        List<FoodFeature> featuresInQuery = foodFeatureRepository.findByQueryContainingFeature(noWhiteKeyword);

        // "왕십리 소화가 잘 되는 음식"과 같은 예
        if (initialResult.response().getMeta().getIs_end() && !featuresInQuery.isEmpty()) {
            return handleFeatureBasedRegionalSearch(request, noWhiteKeyword, featuresInQuery, filter);
        }

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query(featureResult.getProcessedKeyword())
                .addedFilterFromQuery(featureResult.getAddedFeatureFromQuery())
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        List<Document> documents = getDocsOnLoopByQuery(
                request.getX(), request.getY(), featureResult.getProcessedKeyword());
        searchInfo.setApiCallCount(apiCallService.getAndResetApiCallCount());
        return Pair.of(documents, searchInfo);
    }

    /**
     * 특수 예외 케이스
     * - 예 : "왕십리 속 편한 음식"
     */
    private Pair<List<Document>, SearchInfo> handleFeatureBasedRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            String noWhiteKeyword,
            List<FoodFeature> featuresInQuery,
            StoreRequestDto.SearchFilterDto filter) {

        FoodFeature foodFeature = featuresInQuery.get(0);
        List<Long> categoryIdList = searchFeatureService.getCategoryIdList(foodFeature);
        filter.getCategoryIdList().addAll(categoryIdList);

        // lastquery <- "왕십리"
        String lastQuery = noWhiteKeyword.replace(foodFeature.getName(), "");

        SearchInfo baseInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query(lastQuery)
                .addedFilterFromQuery(foodFeature.getName())
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        // 반복문, 지역이 발견되지 않으면 첫트에 중단
        return getDocsOnLoopByQueryThatMustBeRegion(
                request.getX(), request.getY(), lastQuery, baseInfo);
    }

    /**
     * 지역 기반 검색 처리
     */
    private Pair<List<Document>, SearchInfo> handleRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            SearchFeatureService.FeatureExtractionResult featureResult) {

        Pair<String, String> coordinates = searchLocationService
                .getCoordinatesForRegion(
                        initialResult.selectedAddress(), request.getX(), request.getY());

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(coordinates.getFirst())
                .baseY(coordinates.getSecond())
                .query(featureResult.getProcessedKeyword())
                .addedFilterFromQuery(featureResult.getAddedFeatureFromQuery())
                .otherRegions(initialResult.response().getMeta().getSame_name().getRegion())
                .selectedRegion(initialResult.selectedAddress())
                .build();

        List<Document> documents = getDocsOnLoopByQuery(
                coordinates.getFirst(), coordinates.getSecond(), featureResult.getProcessedKeyword());
        searchInfo.setApiCallCount(apiCallService.getAndResetApiCallCount());
        return Pair.of(documents, searchInfo);
    }

    private List<Document> getDocsOnLoopByQuery(String x, String y, String query) {

        if (query == null || query.isBlank()) {
            return searchLocationService.getDocsOnLoopByLocation(x, y);
        }

        List<Document> documents = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                    query, x, y, pageIter++, "accuracy", "FD6");
            apiCallService.incrementApiCallCount();

            documents.addAll(kakaoList.getDocuments());
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return documents;
    }

    private Pair<List<Document>, SearchInfo> getDocsOnLoopByQueryThatMustBeRegion(
            String x, String y, String query, SearchInfo baseInfo) {
        List<Document> documents = new ArrayList<>();

        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " " + "식당", x, y, 1, "accuracy", "FD6");
        apiCallService.incrementApiCallCount();

        if (kakaoList.getMeta().getSame_name().getRegion().isEmpty()) {
            baseInfo.setApiCallCount(apiCallService.getAndResetApiCallCount());
            return Pair.of(documents, baseInfo);
        } // 지역명이 추출이 안될 경우 스톱

        String selectedAddress = kakaoList.getMeta().getSame_name().getSelected_region();
        List<String> otherRegions = kakaoList.getMeta().getSame_name().getRegion();

        Pair<String, String> coordinates = searchLocationService
                .getCoordinatesForRegion(selectedAddress, x, y);

        int pageIter = 1;
        while (pageIter <= 3) {
            kakaoList = storeApiClient.getKakaoByLocation(coordinates.getFirst(), coordinates.getSecond(),
                    pageIter++, "accuracy", "FD6");
            apiCallService.incrementApiCallCount();

            documents.addAll(kakaoList.getDocuments());
            if (kakaoList.getMeta().getIs_end())
                break;
        }

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(coordinates.getFirst())
                .baseY(coordinates.getSecond())
                .query(query)
                .otherRegions(otherRegions)
                .selectedRegion(selectedAddress)
                .apiCallCount(apiCallService.getAndResetApiCallCount())
                .build();

        return Pair.of(documents, searchInfo);
    }
}
