package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.MemberDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberDiseaseRepository extends JpaRepository<MemberDisease, Long> {
    List<MemberDisease> findByMember(Member member);
}
