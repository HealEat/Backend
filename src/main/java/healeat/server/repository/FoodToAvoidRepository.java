package healeat.server.repository;

import healeat.server.domain.FoodToAvoid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodToAvoidRepository extends JpaRepository<FoodToAvoid, Long> {
}
