package healeat.server.repository;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface HealthPlanRepository extends JpaRepository<HealthPlan, Long> {
    Page<HealthPlan> findAllByMember(Member member, PageRequest pageRequest);
    List<HealthPlan> findByMember(Member member);
}
