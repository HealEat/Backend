package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    // 특정 회원이 동의한 약관 리스트 조회
    List<MemberTerm> findByMember(Member member);
}
