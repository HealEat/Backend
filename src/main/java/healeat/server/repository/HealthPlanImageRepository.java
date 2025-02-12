package healeat.server.repository;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthPlanImageRepository extends JpaRepository<HealthPlanImage, Long> {
}
