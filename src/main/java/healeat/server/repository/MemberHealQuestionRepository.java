package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.domain.enums.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MemberHealQuestionRepository extends JpaRepository<MemberHealQuestion, Integer> {
    List<MemberHealQuestion> findByMember(Member member);
}
