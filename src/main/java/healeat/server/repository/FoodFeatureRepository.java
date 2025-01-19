package healeat.server.repository;

import healeat.server.domain.FoodFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface FoodFeatureRepository extends JpaRepository<FoodFeature, Long> {
    List<FoodFeature> findAll();

    Optional<FoodFeature> findByName(String name);

    @Query("SELECT f FROM FoodFeature f " +
            "WHERE REPLACE(:query, ' ', '') LIKE CONCAT('%', f.name, '%') " +
            "AND LENGTH(f.name) >= 2")
    List<FoodFeature> findByQueryContainingFeature(@Param("query") String query);
}
