package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHealthInfoHandler;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DiseaseFeignClient diseaseFeignClient;

    @Transactional
    public void updateMemberDiseases(Long memberId, String searchText){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHealthInfoHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // Feign Client 호출
        List<String> diseases = diseaseFeignClient.getDiseases(
                10,
                1,
                1,
                1,  // 양방
                "SICK_NM",
                searchText  // 내가 검색한 질병명
        ).extractDiseaseNames();

        // Member 도메인의 diseases 필드 업데이트
        member.getDiseases().clear();
        member.getDiseases().addAll(diseases);

        memberRepository.save(member);
    }
}
