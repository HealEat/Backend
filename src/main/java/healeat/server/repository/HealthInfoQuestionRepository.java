package healeat.server.repository;

import healeat.server.domain.MemberHealthInfoQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInfoQuestionRepository extends JpaRepository<MemberHealthInfoQuestion, Long> {
}
