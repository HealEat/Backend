package healeat.server.repository;

import healeat.server.domain.HealthInfoAnswer;
import healeat.server.domain.MemberHealQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInfoAnswerRepository extends JpaRepository<HealthInfoAnswer, Long> {

    void deleteByMemberHealQuestion(MemberHealQuestion question);
}
