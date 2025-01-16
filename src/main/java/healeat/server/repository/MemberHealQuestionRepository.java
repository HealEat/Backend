package healeat.server.repository;

import healeat.server.domain.MemberHealQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHealQuestionRepository extends JpaRepository<MemberHealQuestion, Integer> {
}
