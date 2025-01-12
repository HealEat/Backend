package healeat.server.repository;

import healeat.server.domain.mapping.MemberMealNeeded;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMealNeededRepository extends JpaRepository<MemberMealNeeded, Long> {
}
