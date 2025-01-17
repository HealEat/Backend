package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.*;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryServiceImpl implements StoreQueryService {

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

    @Override
    public List<Document> searchByFilter(StoreRequestDto.SearchFilterDto request) {

        int adjustedPage = Math.max(0, request.getPage() - 1); // 페이징에 쓸 adjustedPage

        int kakaoApiPage = 1;

        String originalQuery = request.getQuery();

        // 필터 Set
        Set<Long> categoryIdSet = getCategoryIdListByKeywordIds(
                request.getFeatureIdList(), request.getCategoryIdList());

        List<Document> finalFilteredList = new ArrayList<>();

        if (originalQuery == null || originalQuery.isBlank()) { // 질의어 없음

            while (finalFilteredList.size() < 10 && kakaoApiPage <= 15) {
                KakaoPlaceResponseDto kakaoList = storeApiClient.getStoresSimply(
                        request.getX(),
                        request.getY(),
                        kakaoApiPage++,
                        "distance");
                apiCallCount++;
                // 반복문 로직
                finalFilteredList.addAll(getFilteredKakaoList(kakaoList, categoryIdSet));

                if (kakaoList.getDocuments().isEmpty() || kakaoList.getMeta().getIs_end())
                    return finalFilteredList;
            }

            return finalFilteredList;

        } else if (containsFeature(originalQuery)) { // 질의어가 음식 특징을 포함함
            /*
            case I. 회생 케이스는 단 하나
            "홍대 소화가 잘되는 음식" 와 같은 놈 -> 지역으로 검색, '소화가 잘되는 음식' 필터 추가

            case II. 특수 케이스 브레인스토밍:
            "홍대 왕십리 날음식" -> 지역과 "왕십리 날음식" 그대로 API
            "홍대 야채호빵" -> 지역과 "야채호빵" 그대로 API
            "야채호빵" -> 그대로
            "문지방" -> 그대로
            "라면요리" -> 그대로

            결론 :
            if (지역 떼면, 특징 키워드 그 자체일 때) : 특징 필터 추가, 질의어는 "지역"
            else : "originalQuery" 그대로 API
            */
            KakaoPlaceResponseDto kakaoList = storeApiClient.getStoresByQuery(
                    originalQuery,
                    request.getX(),
                    request.getY(),
                    kakaoApiPage,
                    1,
                    "accuracy");
            apiCallCount++;

            String selectedRegion = kakaoList.getMeta().getSame_name().getSelected_region();
            String target = kakaoList.getMeta().getSame_name().getKeyword();

            String keyword = target.replaceAll("\\s+", ""); // 공백 제거
            Optional<FoodFeature> sameNameFeature = foodFeatureRepository.findByName(keyword);

            if (sameNameFeature.isPresent()) {

                FoodFeature foodFeature = sameNameFeature.get();
                List<Long> categoryIdList = featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                        .map(f_c -> f_c.getFoodCategory().getId())
                        .toList();
                categoryIdSet.addAll(categoryIdList); // 필터 Set에 추가

                String modifiedQuery;
                if (selectedRegion == null) { // 질의어에 지역이 없음. ex) "소화가 잘되는 음식" 으로 검색

                    while (finalFilteredList.size() < 10 && kakaoApiPage <= 45) {
                        kakaoList = storeApiClient.getStoresSimply(  // 쿼리 없이 검색
                                request.getX(),
                                request.getY(),
                                kakaoApiPage++,
                                "distance");
                        apiCallCount++;

                        // 반복문 로직
                        finalFilteredList.addAll(getFilteredKakaoList(kakaoList, categoryIdSet));

                        if (kakaoList.getDocuments().isEmpty() || kakaoList.getMeta().getIs_end())
                            return finalFilteredList;
                    }

                } else {

                    modifiedQuery = originalQuery.replace(target, "").trim(); // 기존에 검색한 지역만 추출

                    while (finalFilteredList.size() < 10 && kakaoApiPage <= 45) {
                        kakaoList = storeApiClient.getStoresByQuery(
                                modifiedQuery, // 지역으로 검색
                                request.getX(),
                                request.getY(),
                                kakaoApiPage++,
                                15,
                                "accuracy");
                        apiCallCount++;

                        // 반복문 로직
                        finalFilteredList.addAll(getFilteredKakaoList(kakaoList, categoryIdSet));

                        if (kakaoList.getDocuments().isEmpty() || kakaoList.getMeta().getIs_end())
                            return finalFilteredList;
                    }

                }

                return finalFilteredList;
            }
        }

        // 질의어 그대로 검색
        // 질의어가 음식 특징을 포함하지만, 지역을 뗐을 때 특징과 동일한 것은 아닌 경우도 포함 (예 : 왕십리 야채빵)
        while (finalFilteredList.size() < 10 && kakaoApiPage <= 15) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getStoresByQuery(
                    originalQuery,
                    request.getX(),
                    request.getY(),
                    kakaoApiPage++,
                    15,
                    "accuracy");
            apiCallCount++;

            // 반복문 로직
            finalFilteredList.addAll(getFilteredKakaoList(kakaoList, categoryIdSet));

            if (kakaoList.getDocuments().isEmpty() || kakaoList.getMeta().getIs_end())
                return finalFilteredList;
        }

        return finalFilteredList;
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

    private List<Document> getFilteredKakaoList(KakaoPlaceResponseDto kakaoList, Set<Long> categoryIdSet) {

        if (categoryIdSet != null && !categoryIdSet.isEmpty())
        {
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

    @Override
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
