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
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Page<Store> searchByFilter(StoreRequestDto.SearchFilterDto request) {

        List<Long> featureIdList = request.getFeatureIdList();

        Set<Long> categoryIdSet = new HashSet<>(
                request.getCategoryIdList().stream().map(foodCategoryRepository::findById)
                        .map(op_c ->
                                op_c.orElseThrow(() -> new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND))
                                        .getId())
                        .toList()
        );

        if (!featureIdList.isEmpty()) { // 선택한 음식 특징 필터가 존재할 때

            for (Long featureId : featureIdList) {
                List<FeatCategoryMap> featCategoryMaps = featCategoryMapRepository.findAllByFoodFeature_Id(featureId);
                List<Long> categoryIdList = featCategoryMaps.stream()
                        .map(f_c -> f_c.getFoodCategory().getId())
                        .toList();
                categoryIdSet.addAll(categoryIdList);
            }
        }

        // 질의어 조사
        String query = request.getQuery();
        // 동일한 이름의 음식 특징
        Optional<FoodFeature> sameNameFeature = foodFeatureRepository.findAll().stream()
                .filter(f -> f.getName().equals(query))
                .findFirst();
        // 동일한 이름의 음식 종류
        Optional<FoodCategory> sameNameCategory = foodCategoryRepository.findAll().stream()
                .filter(c -> c.getName().equals(query))
                .findFirst();

        // 질의어와 동일한 이름의 음식 특징이 있을 때
        if (sameNameFeature.isPresent()) {
            FoodFeature foodFeature = sameNameFeature.get();
            List<Long> categoryIdList = featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                    .map(f_c -> f_c.getFoodCategory().getId())
                    .toList();
            categoryIdSet.addAll(categoryIdList);
        }
        // 질의어와 동일한 이름의 음식 종류가 있을 때
        sameNameCategory.ifPresent(foodCategory -> categoryIdSet.add(foodCategory.getId()));

        List<String> categoryIdList = categoryIdSet.stream()
                .map(id -> foodCategoryRepository.findById(id).get().getName())
                .toList();

        /// ////

        int adjustedPage = Math.max(0, request.getPage() - 1);

//        KakaoPlaceResponseDto kakaoResponse = storeApiClient.getStoresByQuery(
//                request.getUserInput(),
//                request.getX(),
//                request.getY(),
//                adjustedPage,
//                "accuracy");


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
