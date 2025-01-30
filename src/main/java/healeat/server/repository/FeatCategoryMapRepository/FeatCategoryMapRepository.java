package healeat.server.repository.FeatCategoryMapRepository;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FeatCategoryMapRepository extends JpaRepository<FeatCategoryMap, Long>, FeatCategoryMapRepositoryCustom {

    List<FeatCategoryMap> findByFoodCategory(FoodCategory foodCategory);

    List<FeatCategoryMap> findAllByFoodFeature(FoodFeature foodFeature);
}
