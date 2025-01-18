package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.*;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
     *  -> 이후 컨트롤러에 위임
     */
    public Page<Document> getSortedDocuments(Integer page, StoreRequestDto.SearchKeywordDto request, Float minRating) {

        List<Document> documents = getDocumentsByKeywords(request);
        int adjustedPage = Math.max(0, page - 1); // 페이징에 쓸 adjustedPage

        return null;
    }

    /**
     * API 호출 핵심 로직
     * @param request
     * @return 쿼리 또는 키워드 필터로 선택한
     * 카카오 가게 리스트
     */
    public List<Document> getDocumentsByKeywords(StoreRequestDto.SearchKeywordDto request) {

        String query = request.getQuery();

        // 필터 Set
        Set<Long> categoryIdSet = getCategoryIdListByKeywordIds(
                request.getFeatureIdList(), request.getCategoryIdList());

        if (query == null || query.isBlank()) { // 질의어 없음

            return getDocsOnLoopByLocation(request, categoryIdSet, 1);
        }
        int currentPage = 1;
        List<Document> finalFilteredList = new ArrayList<>();
        if (containsFeature(query)) { // 질의어가 음식 특징을 포함함 ex) "홍대 소화가잘 되는 음식", "왕십리 야채빵"
            /*
            case I. 회생 케이스는 단 하나
            "홍대 소화가 잘되는 음식" 와 같은 놈 -> [지역명 + " 식당"]으로 검색, '소화가 잘되는 음식' 필터 추가

            case II. 특수 케이스 브레인스토밍:
            "홍대 왕십리 날음식" -> 지역과 "왕십리 날음식" 그대로 API
            "홍대 야채호빵" -> 지역과 "야채호빵" 그대로 API
            "야채호빵" -> 그대로
            "문지방" -> 그대로
            "라면요리" -> 그대로

            결론 :
            if (지역 떼면, 특징 키워드 그 자체일 때) : 특징 필터 추가, 질의어는 [지역명 + " 식당"]
            else : [originalQuery] 그대로 API
            */
            KakaoPlaceResponseDto kakaoList = storeApiClient
                    .getKakaoByQuery(query, request.getX(), request.getY(), 1, "accuracy"); // 지역 검사용 첫 페이지
            apiCallCount++;

            String selectedRegion = kakaoList.getMeta().getSame_name().getSelected_region(); // 지역명

            String keyword = kakaoList.getMeta().getSame_name().getKeyword()   // 지역명 뺀 나머지 질의어
                    .replaceAll("\\s+", ""); // 공백 제거

            Optional<FoodFeature> sameNameFeature = foodFeatureRepository.findByName(keyword);
            if (sameNameFeature.isPresent()) { // 포함하는 걸 넘어서 정확히 일치한다면

                FoodFeature foodFeature = sameNameFeature.get();
                List<Long> categoryIdList = featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                        .map(f_c -> f_c.getFoodCategory().getId())
                        .toList();
                categoryIdSet.addAll(categoryIdList); // 해당 특징을 필터 Set에 추가

                if (selectedRegion == null) { // 질의어에 지역이 없음. ex) "소화가 잘되는 음식"이었던 것-> 쿼리 없이 검색

                    return getDocsOnLoopByLocation(request, categoryIdSet, 1);

                } else { // 질의어에 지역이 있음.
                    // [지역명 + " " + "식당"] 으로 검색
                    return getDocsOnLoopByQuery(request, categoryIdSet, 1, selectedRegion + " " + "식당");
                }
            }

            // 그 외에는 지역 검사용에 썼던 쿼리와 동일하므로
            finalFilteredList.addAll(
                    filterAndGetDocuments(kakaoList, categoryIdSet)); // 지역 검사용 결과도 활용
            currentPage = 2;
        }
        // 질의어 그대로 검색
        // 지역명 뺀 나머지 질의어가 음식 특징을 포함하지만, 정확히 일치한 것은 아닌 경우도 포함 (예 : 왕십리 야채빵)
        finalFilteredList.addAll(
                getDocsOnLoopByQuery(request, categoryIdSet, currentPage, query));
        return finalFilteredList;
    }

    public KakaoPlaceResponseDto getKakaoByLocation(StoreRequestDto.SearchKeywordDto request, Integer page) {
        apiCallCount++;
        return storeApiClient.getKakaoByLocation(request.getX(), request.getY(), page, "distance");
    }

    private List<Document> getDocsOnLoopByLocation(StoreRequestDto.SearchKeywordDto request, Set<Long> categoryIdSet, Integer page) {
        List<Document> filteredList = new ArrayList<>();
        int pageIter = page;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoList = getKakaoByLocation(request, pageIter++);
            apiCallCount++;

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    private List<Document> getDocsOnLoopByQuery(StoreRequestDto.SearchKeywordDto request, Set<Long> categoryIdSet, Integer page, String query) {
        List<Document> filteredList = new ArrayList<>();
        int pageIter = page;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                    query,
                    request.getX(),
                    request.getY(),
                    pageIter++,
                    "accuracy");
            apiCallCount++;

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    private Set<Long> getCategoryIdListByKeywordIds(List<Long> featureIdList, List<Long> categoryIdList) {

        // 계산용 카테고리 ID Set
        Set<Long> categoryIdSet = new HashSet<>();
        if (categoryIdList != null && !categoryIdList.isEmpty()) {
            categoryIdSet.addAll(categoryIdList.stream()
                    .map(foodCategoryRepository::findById)
                    .map(op_c -> op_c.orElseThrow(
                            () -> new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND)
                    ).getId())
                    .toList());
        }

        if (featureIdList != null && !featureIdList.isEmpty()) { // 선택한 음식 특징 필터가 존재할 때

            featureIdList.stream()
                    .map(featCategoryMapRepository::findAllByFoodFeature_Id)
                    .map(featCategoryMaps -> featCategoryMaps.stream()
                            .map(f_c -> f_c.getFoodCategory().getId())
                            .toList())
                    .forEach(categoryIdSet::addAll);
        }

        return categoryIdSet;
    }

    private Boolean containsFeature(String query) {
        return !foodFeatureRepository.findByQueryContainingFeature(query).isEmpty();
    }

    private List<Document> filterAndGetDocuments(KakaoPlaceResponseDto kakaoList, Set<Long> categoryIdSet) {

        if (kakaoList.getMeta().getIs_end()) {

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
