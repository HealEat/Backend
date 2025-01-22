package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto.SearchInfo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final StoreApiClient storeApiClient;
    private final SearchLocationService searchLocationService;
    private final SearchFeatureService searchFeatureService;

    private final FoodFeatureRepository foodFeatureRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    /**
     * 메인 검색 로직
     */
    public Pair<List<Document>, SearchInfo> searchStores(StoreRequestDto.SearchKeywordDto request) {

        String query = request.getQuery();
        Set<Long> categoryIdSet = searchFeatureService.getCategoryIdListByKeywordIds(
                request.getFeatureIdList(),
                request.getCategoryIdList()
        );

        if (query == null || query.isBlank()) {
            return handleEmptyQuery(request, categoryIdSet);
        }

        InitialSearchResult initialResult = performInitialSearch(request, query);
        SearchFeatureService.FeatureExtractionResult featureResult = searchFeatureService.extractFeatures(
                initialResult.keyword(),
                categoryIdSet
        );

        return performFinalSearch(request, initialResult, featureResult);
    }

    /**
     * 빈 쿼리 처리 (캐시 대상)
     */
    private Pair<List<Document>, SearchInfo> handleEmptyQuery(StoreRequestDto.SearchKeywordDto request, Set<Long> categoryIdSet) {
        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query(request.getQuery())
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        List<Document> documents = searchLocationService
                .getDocsOnLoopByLocation(request.getX(), request.getY(), categoryIdSet, 1);
        return Pair.of(documents, searchInfo);
    }

    /**
         * 초기 검색 결과를 담는 클래스 (캐시 결과 포함 가능)
         */
        @Builder
        private record InitialSearchResult(KakaoPlaceResponseDto response,
                                           String selectedAddress,
                                           String keyword,
                                           List<Document> initialDocuments) {
    }

    /**
     * 초기 검색 수행 (캐시 대상)
     */
    private InitialSearchResult performInitialSearch(StoreRequestDto.SearchKeywordDto request, String query) {
        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " 식당",
                request.getX(), request.getY(), 1, "accuracy", "FD6");

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
            SearchFeatureService.FeatureExtractionResult featureResult) {

        /*
            "왕십리 소화가 잘되는 음식"
            -> 카카오 로컬 API에서 지역 인식을 하지 못함.
            따라서 handleNonRegionalSearch에서
            이와 같은 상황은 특수 케이스 분류로 처리
        */
        if (initialResult.selectedAddress() == null || initialResult.selectedAddress().isBlank()) {
            // "근처 맛집", "부드러운 음식", "왕십리 소화가 잘되는 음식"
            return handleNonRegionalSearch(request, initialResult, featureResult);
        } else {
            // "아차산 맛집", "경대병원 죽", "홍대 소화가 잘되는 음식"
            return handleRegionalSearch(request, initialResult, featureResult);
        }
    }

    /**
     * 지역 없는 검색 처리 (캐시 대상)
     */
    private Pair<List<Document>, SearchInfo> handleNonRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            SearchFeatureService.FeatureExtractionResult featureResult) {

        String noWhiteKeyword = featureResult.processedKeyword().replaceAll("\\s+", "");
        List<FoodFeature> featuresInQuery = foodFeatureRepository.findByQueryContainingFeature(noWhiteKeyword);

        // "왕십리 소화가 잘 되는 음식"과 같은 예
        if (initialResult.response().getMeta().getIs_end() && !featuresInQuery.isEmpty()) {
            return handleFeatureBasedRegionalSearch(request, noWhiteKeyword, featuresInQuery, featureResult.updatedCategoryIds());
        }

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query(request.getQuery())
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        List<Document> documents = getDocsOnLoopByQuery(
                request.getX(), request.getY(),
                featureResult.updatedCategoryIds(),
                1, featureResult.processedKeyword());

        return Pair.of(documents, searchInfo);
    }

    /**
     * 특징 기반 지역 검색 처리 (캐시 대상)
     */
    private Pair<List<Document>, SearchInfo> handleFeatureBasedRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            String noWhiteKeyword,
            List<FoodFeature> featuresInQuery,
            Set<Long> categoryIdSet) {

        FoodFeature foodFeature = featuresInQuery.get(0);
        categoryIdSet.addAll(searchFeatureService.getCategoryIdList(foodFeature));

        String lastQuery = noWhiteKeyword.replace(foodFeature.getName(), "");

        SearchInfo baseInfo = SearchInfo.builder()
                .baseX(request.getX())
                .baseY(request.getY())
                .query(request.getQuery())
                .otherRegions(List.of(""))
                .selectedRegion("")
                .build();

        // 반복문, 지역이 발견되지 않으면 첫트에 중단
        return getDocsOnLoopByQueryThatMustBeRegion(
                request.getX(), request.getY(), categoryIdSet, lastQuery,
                baseInfo);
    }

    /**
     * 지역 기반 검색 처리 (캐시 대상)
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
                .query(request.getQuery())
                .otherRegions(initialResult.response().getMeta().getSame_name().getRegion())
                .selectedRegion(initialResult.selectedAddress())
                .build();

        List<Document> documents = getDocsOnLoopByQuery(
                coordinates.getFirst(), coordinates.getSecond(),
                featureResult.updatedCategoryIds(),
                1, featureResult.processedKeyword());

        return Pair.of(documents, searchInfo);
    }

    private List<Document> getDocsOnLoopByQuery(String x, String y, Set<Long> categoryIdSet, Integer fromPage, String query) {
        if (query == null || query.isBlank()) return searchLocationService
                .getDocsOnLoopByLocation(x, y, categoryIdSet, fromPage);
        List<Document> filteredList = new ArrayList<>();
        int pageIter = fromPage;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                    query, x, y, pageIter++, "accuracy", "FD6");

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    private List<Document> filterAndGetDocuments(KakaoPlaceResponseDto kakaoList, Set<Long> categoryIdSet) {

        return getDocuments(kakaoList, categoryIdSet, foodCategoryRepository);
    }

    static List<Document> getDocuments(
            KakaoPlaceResponseDto kakaoList,
            Set<Long> categoryIdSet,
            FoodCategoryRepository foodCategoryRepository) {

        if (kakaoList.getDocuments().isEmpty()) {
            return List.of();
        }

        if (categoryIdSet != null && !categoryIdSet.isEmpty()) {
            List<String> categoryNameList = categoryIdSet.stream()
                    .map(foodCategoryRepository::findById)
                    .map(op_c -> op_c.get().getName())
                    .toList();

            return kakaoList.getDocuments().stream()
                    .filter(document -> categoryNameList.stream().anyMatch(document.getCategory_name()::contains))
                    .toList();
        } else {
            return kakaoList.getDocuments();
        }
    }

    private Pair<List<Document>, SearchInfo> getDocsOnLoopByQueryThatMustBeRegion(
            String x, String y, Set<Long> categoryIdSet, String query, SearchInfo baseInfo) {
        List<Document> filteredList = new ArrayList<>();

        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " " + "식당", x, y, 1, "accuracy", "FD6");

        if (kakaoList.getMeta().getSame_name().getRegion().isEmpty()) {
            return Pair.of(filteredList, baseInfo);
        } // 지역명이 추출이 안될 경우 스톱

        String selectedAddress = kakaoList.getMeta().getSame_name().getSelected_region();
        List<String> otherRegions = kakaoList.getMeta().getSame_name().getRegion();

        Pair<String, String> coordinates = searchLocationService
                .getCoordinatesForRegion(selectedAddress, x, y);

        int pageIter = 1;
        while (pageIter <= 3) {
            kakaoList = storeApiClient.getKakaoByLocation(coordinates.getFirst(), coordinates.getSecond(),
                    pageIter++, "accuracy", "FD6");

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(coordinates.getFirst())
                .baseY(coordinates.getSecond())
                .query(baseInfo.getQuery())
                .otherRegions(otherRegions)
                .selectedRegion(selectedAddress)
                .build();

        return Pair.of(filteredList, searchInfo);
    }
}
