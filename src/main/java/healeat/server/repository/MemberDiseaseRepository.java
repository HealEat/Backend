package healeat.server.repository;

import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.MemberDisease;
import healeat.server.web.dto.MemberDiseaseResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberDiseaseRepository extends JpaRepository<MemberDisease, Long> {
    Boolean existsByMemberAndDisease(Member member, Disease disease);
    List<MemberDisease> findByMember(Member member);
}
