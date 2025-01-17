package healeat.server.repository;

import healeat.server.domain.FoodFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodFeatureRepository extends JpaRepository<FoodFeature, Long> {
}
