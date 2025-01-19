package healeat.server.repository;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FeatCategoryMapRepository extends JpaRepository<FeatCategoryMap, Long> {
    List<FeatCategoryMap> findAllByFoodFeature(FoodFeature foodFeature);

    List<FeatCategoryMap> findAllByFoodFeature_Id(Long foodFeatureId);

    List<FeatCategoryMap> findByFoodCategory_Name(String foodCategoryName);

    List<FeatCategoryMap> findByFoodCategory(FoodCategory foodCategory);
}
