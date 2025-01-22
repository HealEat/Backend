package healeat.server.repository;

import healeat.server.domain.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface HealthPlanRepository extends JpaRepository<HealthPlan, Long> {
    List<HealthPlan> findByMemberId(Long memberId);
}
