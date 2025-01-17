package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.FoodFeatureHandler;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.HealthPlan;
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
public class FoodFeatureService {

    private final FoodFeatureRepository foodFeatureRepository;

    public List<FoodFeature> getAllFoodFeatures() {
        return foodFeatureRepository.findAll();
    }
    //혹시나 필요하면 쓸 getById
    public FoodFeature getFoodFeatureById(Long id) {
        return foodFeatureRepository.findById(id).orElseThrow(() ->
                new FoodFeatureHandler(ErrorStatus.FOOD_FEATURE_NOT_FOUND));
    }

}
