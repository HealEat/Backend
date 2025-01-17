package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.HealthPlan;
import healeat.server.repository.FoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    public List<FoodCategory> getAllFoodCategories() {
        return foodCategoryRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public FoodCategory getFoodCategoryById(Long id) {
        return foodCategoryRepository.findById(id).orElseThrow(() ->
                new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND));
    }
}
