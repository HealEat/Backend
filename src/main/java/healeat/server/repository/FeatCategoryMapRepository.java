package healeat.server.repository;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatCategoryMapRepository extends JpaRepository<FeatCategoryMap, Long> {

    List<FeatCategoryMap> findByFoodCategory(FoodCategory foodCategory);

    List<FeatCategoryMap> findAllByFoodFeature(FoodFeature foodFeature);
}
