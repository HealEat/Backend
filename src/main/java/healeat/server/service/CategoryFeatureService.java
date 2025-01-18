package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.FoodFeatureHandler;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.web.dto.SearchPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryFeatureService {

    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodFeatureRepository foodFeatureRepository;

    public List<FoodCategory> getAllFoodCategories() {
        return foodCategoryRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public FoodCategory getFoodCategoryById(Long id) {
        return foodCategoryRepository.findById(id).orElseThrow(() ->
                new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND));
    }

    public List<FoodFeature> getAllFoodFeatures() {
        return foodFeatureRepository.findAll();
    }
    //혹시나 필요하면 쓸 getById
    public FoodFeature getFoodFeatureById(Long id) {
        return foodFeatureRepository.findById(id).orElseThrow(() ->
                new FoodFeatureHandler(ErrorStatus.FOOD_FEATURE_NOT_FOUND));
    }

    public SearchPageResponseDto.FoodCategoryListResponseDto getAllFoodCategoryPage() {

        List<FoodCategory> foodCategories = foodCategoryRepository.findAll();

        return SearchPageConverter.toFoodCategoryListResponseDto(foodCategories);
    }

    public SearchPageResponseDto.FoodFeatureListResponseDto getAllFoodFeaturePage() {

        List<FoodFeature> foodFeatures = foodFeatureRepository.findAll();

        return SearchPageConverter.toFoodFeatureListResponseDto(foodFeatures);
    }
}
