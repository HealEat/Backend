package healeat.server.repository;

import healeat.server.domain.mapping.MemberFoodToAvoid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFoodToAvoidRepository extends JpaRepository<MemberFoodToAvoid, Long> {
}
