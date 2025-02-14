package healeat.server.repository;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthPlanRepository extends JpaRepository<HealthPlan, Long> {
    List<HealthPlan> findByMember(Member member);

    Page<HealthPlan> findAllByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
}
