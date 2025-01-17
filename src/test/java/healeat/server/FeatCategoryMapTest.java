package healeat.server;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.repository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional(readOnly = true)
public class FeatCategoryMapTest {

    @Autowired
    private FeatCategoryMapRepository featCategoryMapRepository;
    @Autowired
    private FoodFeatureRepository foodFeatureRepository;

    @Test
    public void 음식종류_출력() throws Exception {

        List<FoodFeature> foodFeatures = foodFeatureRepository.findAll();

        for (FoodFeature foodFeature : foodFeatures) {
            List<FeatCategoryMap> featCategoryMapList = featCategoryMapRepository.findAllByFoodFeature(foodFeature);
            List<String> foodCategoryNames = featCategoryMapList.stream().map(FeatCategoryMap::getFoodCategory).map(FoodCategory::getName).toList();
            System.out.println(foodFeature.getName());
            for (String foodCategoryName : foodCategoryNames) {
                System.out.println(foodCategoryName);
            }
        }
    }
}
