package healeat.server.repository;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatCategoryMapRepository extends JpaRepository<FeatCategoryMap, Long> {
    List<FeatCategoryMap> findAllByFoodFeature(FoodFeature foodFeature);
}
