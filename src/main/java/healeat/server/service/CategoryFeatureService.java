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
    private final SearchPageConverter searchPageConverter;

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

        List<FoodCategory> foodCategories = getAllFoodCategories();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(searchPageConverter::toFoodCategoryResponseDto)
                .toList();

        return SearchPageResponseDto.FoodCategoryListResponseDto.builder()
                .FoodCategoryList(foodCategoryList)
                .build();
    }

    public SearchPageResponseDto.FoodFeatureListResponseDto getAllFoodFeaturePage() {

        List<FoodFeature> foodFeatures = getAllFoodFeatures();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(searchPageConverter::toFoodFeatureResponseDto)
                .toList();

       return SearchPageResponseDto.FoodFeatureListResponseDto.builder()
                .FoodFeatureList(foodFeatureList)
                .build();
    }
}
