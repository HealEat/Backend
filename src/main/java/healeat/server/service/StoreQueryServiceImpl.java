package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.FoodFeatureHandler;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.*;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.KakaoXYResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static healeat.server.web.dto.StoreResonseDto.*;
import static healeat.server.web.dto.StoreResonseDto.StorePreviewDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class StoreQueryServiceImpl {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final StoreApiClient storeApiClient;
    private final FoodFeatureRepository foodFeatureRepository;
    private final FeatCategoryMapRepository featCategoryMapRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    private int apiCallCount = 0;

    public int getApiCallCount() {
        return apiCallCount;
    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     * -> 이후 컨트롤러에 위임
     */
    // 리뷰가 존재 -> DB와 매핑된 데이터 먼저 표시
    // distance가 존재 -> distance로 정렬
    public Pair<Page<StorePreviewDto>, SearchInfo> mapDocumentWithDB(
            @CheckPage Integer page,
            StoreRequestDto.SearchKeywordDto request,
            Float minRating) {

        Pair<List<Document>, SearchInfo> docInfoPair = getDocumentsByKeywords(request);
        List<Document> documents = docInfoPair.getFirst();
        SearchInfo searchInfo = docInfoPair.getSecond();

        // api 호출 횟수 기록 후 초기화
        searchInfo.setApiCallCount(getApiCallCount());
        apiCallCount = 0;

        int adjustedPage = Math.max(0, page - 1); // 페이징에 쓸 adjustedPage

        /**
         * 북마크 구현 필요 <- 멤버 (스프링 시큐리티 Authorization?)
         */
        List<StorePreviewDto> storePreviewDtoList = documents.stream()
                .map(document -> {

                    Set<FoodFeature> featureSet = foodCategoryRepository.findAll().stream()
                            .filter(fc -> document.getCategory_name().contains(fc.getName()))
                            .flatMap(fc -> {
                                List<FeatCategoryMap> featCategoryMaps = featCategoryMapRepository.findByFoodCategory(fc);
                                return featCategoryMaps.stream()
                                        .map(FeatCategoryMap::getFoodFeature);
                            })
                            .collect(Collectors.toSet());

                    List<String> features;
                    if (featureSet.isEmpty()) { // DB에 등록이 되지 않은 음식 카테고리면
                        features = List.of("");
                    } else {
                        features = featureSet.stream()
                                .map(FoodFeature::getName)
                                .toList();
                    }

                    StorePreviewDto.StorePreviewDtoBuilder builder = StorePreviewDto.builder()
                            .id(Long.parseLong(document.getId()))
                            .place_name(document.getPlace_name())
                            .category_name(document.getCategory_name())
                            .phone(document.getPlace_url())
                            .address_name(document.getAddress_name())
                            .road_address_name(document.getRoad_address_name())
                            .x(document.getX())
                            .y(document.getY())
                            .place_url(document.getPlace_url())
                            .distance(document.getDistance())
                            .features(features)
                            .reviewCount(0)
                            .totalScore(0.0f)
                            .sickScore(0.0f)
                            .vegetScore(0.0f)
                            .dietScore(0.0f)
                            .isBookMarked(false);

                    Optional<Store> storeOptional = storeRepository.findById(Long.parseLong(document.getId()));

                    // 여기부터 healeat Store 데이터
                    storeOptional.ifPresent(store -> {
                        builder.reviewCount(store.getReviewCount())
                                .totalScore(store.getTotalScore())
                                .sickScore(store.getSickScore())
                                .vegetScore(store.getVegetScore())
                                .dietScore(store.getDietScore())
                                .isBookMarked(false);
                    });

                    return builder.build();
                })
                .toList();

        // 정렬 I. 리뷰가 존재하는 가게들 우선
        List<StorePreviewDto> serverFirstList = new ArrayList<>(storePreviewDtoList.stream()
                .filter(s -> (s.getReviewCount() > 0) && (s.getTotalScore() >= minRating))
                .toList());

        List<StorePreviewDto> remainList = storePreviewDtoList.stream()
                .filter(s -> s.getReviewCount() == 0).toList();

        serverFirstList.addAll(remainList);

        Comparator<StorePreviewDto> comparator = Comparator
                // 정렬 II. 가까운 순
                .comparing((StorePreviewDto s) -> {
                    String distance = s.getDistance();
                    // 거리 정보가 없는 경우 가장 멀리 정렬되도록 Integer.MAX_VALUE 반환
                    return distance == null || distance.isEmpty()
                            ? Integer.MAX_VALUE
                            : Integer.parseInt(distance);
                })
                // (정렬 III. 전체 평점 높은 순)
                .thenComparing(StorePreviewDto::getTotalScore, Comparator.reverseOrder());

        // 동적 쿼리(QueryDSL)에 대해 생각할 필요 O
        // (기본= 전체 평점), 드롭다운 : 질병 관리 평점, 베지테리언 평점, 다이어터 평점
        Page<StorePreviewDto> storePreviewDtoPage =
                CustomPagination.toPage(serverFirstList, adjustedPage, 10, comparator);

        return Pair.of(storePreviewDtoPage, searchInfo);
    }

    /**
     * 메인 검색 로직
     */
    public Pair<List<Document>, SearchInfo> getDocumentsByKeywords(StoreRequestDto.SearchKeywordDto request) {
        String query = request.getQuery();
        Set<Long> categoryIdSet = getCategoryIdListByKeywordIds(request.getFeatureIdList(), request.getCategoryIdList());

        // 빈 쿼리 처리
        if (query == null || query.isBlank()) {
            return handleEmptyQuery(request, categoryIdSet);
        }

        // 초기 검색 수행
        InitialSearchResult initialResult = performInitialSearch(request, query);

        // 특징 추출 및 카테고리 추가
        FeatureExtractionResult featureResult = extractFeatures(initialResult.getKeyword(), categoryIdSet);

        // 지역 기반 또는 키워드 기반 검색 수행
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

        List<Document> documents = getDocsOnLoopByLocation(request.getX(), request.getY(), categoryIdSet, 1);
        return Pair.of(documents, searchInfo);
    }

    /**
     * 초기 검색 결과를 담는 클래스 (캐시 결과 포함 가능)
     */
    @Getter
    @Builder
    private static class InitialSearchResult {
        private final KakaoPlaceResponseDto response;
        private final String selectedAddress;
        private final String keyword;
        private final List<Document> initialDocuments;
    }

    /**
     * 초기 검색 수행 (캐시 대상)
     */
    private InitialSearchResult performInitialSearch(StoreRequestDto.SearchKeywordDto request, String query) {
        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " 식당",
                request.getX(), request.getY(), 1, "accuracy", "FD6");
        apiCallCount++;

        return InitialSearchResult.builder()
                .response(kakaoList)
                .selectedAddress(kakaoList.getMeta().getSame_name().getSelected_region())
                .keyword(kakaoList.getMeta().getSame_name().getKeyword().replaceAll(" 식당", ""))
                .initialDocuments(new ArrayList<>(kakaoList.getDocuments()))
                .build();
    }

    /**
     * 특징 추출 결과를 담는 record
     */
    @Builder
    private record FeatureExtractionResult(Set<Long> updatedCategoryIds,
                                           String processedKeyword,
                                           boolean containsFeature) {
    }

    /**
     * 특징 추출 및 카테고리 추가 (캐시 대상)
     */
    private FeatureExtractionResult extractFeatures(String keyword, Set<Long> categoryIdSet) {
        String noWhiteKeyword = keyword.replaceAll("\\s+", "");
        Optional<FoodFeature> foodFeatureOptional = foodFeatureRepository.findByName(noWhiteKeyword);

        Set<Long> updatedCategoryIds = new HashSet<>(categoryIdSet);
        String processedKeyword = keyword;
        boolean containsFeature = false;

        // 쿼리에 지역명을 제외하고 음식 특징이 존재
        //  -> 필터에 추가, 카카오 API 쿼리에서는 제거
        if (foodFeatureOptional.isPresent()) {
            updatedCategoryIds.addAll(getCategoryIdList(foodFeatureOptional.get()));
            processedKeyword = "";
            containsFeature = true;
        }

        return FeatureExtractionResult.builder()
                .updatedCategoryIds(updatedCategoryIds)
                .processedKeyword(processedKeyword)
                .containsFeature(containsFeature)
                .build();
    }

    /**
     * 최종 검색 수행
     */
    private Pair<List<Document>, SearchInfo> performFinalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            FeatureExtractionResult featureResult) {

        /*
            "왕십리 소화가 잘되는 음식"
            -> 카카오 로컬 API에서 지역 인식을 하지 못함.
            따라서 handleNonRegionalSearch에서
            이와 같은 상황은 특수 케이스 분류로 처리
        */
        if (initialResult.getSelectedAddress() == null || initialResult.getSelectedAddress().isBlank()) {
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
            FeatureExtractionResult featureResult) {

        String noWhiteKeyword = featureResult.processedKeyword().replaceAll("\\s+", "");
        List<FoodFeature> featuresInQuery = foodFeatureRepository.findByQueryContainingFeature(noWhiteKeyword);

        // "왕십리 소화가 잘 되는 음식"과 같은 예
        if (initialResult.getResponse().getMeta().getIs_end() && !featuresInQuery.isEmpty()) {
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
        categoryIdSet.addAll(getCategoryIdList(foodFeature));

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
     * 지역에 대한 좌표 조회 (캐시 대상)
     */
    private Pair<String, String> getCoordinatesForRegion(String address, String x, String y) {
        KakaoXYResponseDto selectedXY = storeApiClient.addressToXY(address, 1, 1);
        apiCallCount++;

        if (selectedXY.getDocuments().isEmpty()) {
            // 주소-좌표 변환 실패시 랜드마크로 시도
            Document landmark = storeApiClient.getLandmarkByQuery(
                            address, x, y, 1, "accuracy")
                    .getDocuments().stream()
                    .findFirst().get(); // 명소를 찾는다. (음식점 한정 X)
            return Pair.of(landmark.getX(), landmark.getY());
        } else {
            KakaoXYResponseDto.Document location = selectedXY.getDocuments().stream().findFirst().get();
            return Pair.of(location.getX(), location.getY());
        }
    }

    /**
     * 지역 기반 검색 처리 (캐시 대상)
     */
    private Pair<List<Document>, SearchInfo> handleRegionalSearch(
            StoreRequestDto.SearchKeywordDto request,
            InitialSearchResult initialResult,
            FeatureExtractionResult featureResult) {

        Pair<String, String> coordinates = getCoordinatesForRegion(
                initialResult.getSelectedAddress(), request.getX(), request.getY());

        SearchInfo searchInfo = SearchInfo.builder()
                .baseX(coordinates.getFirst())
                .baseY(coordinates.getSecond())
                .query(request.getQuery())
                .otherRegions(initialResult.getResponse().getMeta().getSame_name().getRegion())
                .selectedRegion(initialResult.getSelectedAddress())
                .build();

        List<Document> documents = getDocsOnLoopByQuery(
                coordinates.getFirst(), coordinates.getSecond(),
                featureResult.updatedCategoryIds(),
                1, featureResult.processedKeyword());

        return Pair.of(documents, searchInfo);
    }

    private Set<Long> getCategoryIdListByKeywordIds(List<Long> featureIdList, List<Long> categoryIdList) {

        // 계산용 카테고리 ID Set
        Set<Long> categoryIdSet = new HashSet<>();

        if (categoryIdList != null && !categoryIdList.isEmpty()) { // 선택한 음식 종류 필터가 존재할 때
            categoryIdSet.addAll(categoryIdList.stream()
                    .map(foodCategoryRepository::findById)
                    .map(op_c -> op_c.orElseThrow(
                            () -> new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND)
                    ).getId())
                    .toList());
        }

        if (featureIdList != null && !featureIdList.isEmpty()) { // 선택한 음식 특징 필터가 존재할 때

            featureIdList.stream()
                    .map(id -> getCategoryIdList(
                            foodFeatureRepository.findById(id).orElseThrow(() ->
                            new FoodFeatureHandler(ErrorStatus.FOOD_FEATURE_NOT_FOUND))
                            )
                    )
                    .forEach(categoryIdSet::addAll);
        }

        return categoryIdSet;
    }

    private List<Long> getCategoryIdList(FoodFeature foodFeature) {

        return featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                .map(f_c -> f_c.getFoodCategory().getId())
                .toList();
    }

    private List<Document> filterAndGetDocuments(KakaoPlaceResponseDto kakaoList, Set<Long> categoryIdSet) {

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

    private List<Document> getDocsOnLoopByLocation(String x, String y, Set<Long> categoryIdSet, Integer fromPage) {
        List<Document> filteredList = new ArrayList<>();
        int pageIter = fromPage;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByLocation(
                    x, y, pageIter++, "accuracy", "FD6");
            apiCallCount++;

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    private List<Document> getDocsOnLoopByQuery(String x, String y, Set<Long> categoryIdSet, Integer fromPage, String query) {
        if (query == null || query.isBlank()) return getDocsOnLoopByLocation(x, y, categoryIdSet, fromPage);
        List<Document> filteredList = new ArrayList<>();
        int pageIter = fromPage;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                    query, x, y, pageIter++, "accuracy", "FD6");
            apiCallCount++;

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    private Pair<List<Document>, SearchInfo> getDocsOnLoopByQueryThatMustBeRegion(
            String x, String y, Set<Long> categoryIdSet, String query, SearchInfo baseInfo) {
        List<Document> filteredList = new ArrayList<>();

        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " " + "식당", x, y, 1, "accuracy", "FD6");
        apiCallCount++;
        if (kakaoList.getMeta().getSame_name().getRegion().isEmpty()) {
            return Pair.of(filteredList, baseInfo);
        } // 지역명이 추출이 안될 경우 스톱

        String selectedAddress = kakaoList.getMeta().getSame_name().getSelected_region();
        List<String> otherRegions = kakaoList.getMeta().getSame_name().getRegion();

        Pair<String, String> coordinates = getCoordinatesForRegion(selectedAddress, x, y);

        int pageIter = 1;
        while (pageIter <= 3) {
            kakaoList = storeApiClient.getKakaoByLocation(coordinates.getFirst(), coordinates.getSecond(),
                    pageIter++, "accuracy", "FD6");
            apiCallCount++;

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

    public Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        if (sort == null) sort = SortBy.LATEST;

        // 페이지 번호를 0-based로 조정
        int adjustedPage = Math.max(0, page - 1);

        Sort.Direction direction = getSortDirection(sortOrder);

        Sort sorting;
        PageRequest pageable;

        switch (sort) {
            case SICK:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_DiseasesNotEmpty(store, pageable);
            case VEGET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Vegetarian(store, Vegetarian.NONE, pageable);
            case DIET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Diet(store, Diet.NONE, pageable);
            case LATEST:
                sorting = Sort.by(direction, "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStore(store, pageable);
            default:
                throw new SortHandler(ErrorStatus.SORT_NOT_FOUND);
        }
    }

    private Sort.Direction getSortDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
