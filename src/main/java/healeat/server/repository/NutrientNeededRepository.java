package healeat.server.repository;

import healeat.server.domain.NutrientNeeded;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutrientNeededRepository extends JpaRepository<NutrientNeeded, Long> {
}
