package healeat.server.repository;

import healeat.server.domain.MealNeeded;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealNeededRepository extends JpaRepository<MealNeeded, Long> {
}
