package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.TermHandler;
import healeat.server.domain.Member;
import healeat.server.domain.Term;
import healeat.server.domain.mapping.MemberTerm;
import healeat.server.repository.MemberTermRepository;
import healeat.server.repository.TermRepository;
import healeat.server.web.dto.MemberTermResponse;
import healeat.server.web.dto.TermResponse;
import healeat.server.web.dto.TermsAgreeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;

    // 약관 목록 조회 API
    @Transactional(readOnly = true)
    public List<TermResponse> getTerms() {
        return termRepository.findAllByOrderByIsRequiredDesc()
                .stream()
                .map(TermResponse::from)
                .toList();
    }

    // 회원의 약관 동의 저장 API
    @Transactional
    public void saveMemberAgreement(Member member, TermsAgreeRequest request) {
        for (TermsAgreeRequest.TermAgree agree : request.getAgreements()) {
            Term term = termRepository.findById(agree.getTermId())
                    .orElseThrow(() -> new TermHandler(ErrorStatus.INVALID_TERM_ID));

            // 필수 약관(isRequired: true)인데 agree:false 면 예외 발생
            if(term.isRequired() && !agree.getAgree()) {
                throw new TermHandler(ErrorStatus.REQUIRED_TERM_NOT_AGREED);
            }
            // 기존 동의 기록이 있는지 확인
            MemberTerm existingMemberTerm = memberTermRepository.findByMemberAndTerm(member, term);

            if(existingMemberTerm != null) {
                // 기존 데이터 있으면 UPDATE
                existingMemberTerm.updateAgree(agree.getAgree());
            } else {
                // 기존 데이터 없으면 INSERT
                MemberTerm newMemberTerm = MemberTerm.builder()
                        .member(member)
                        .term(term)
                        .agree(agree.getAgree())
                        .build();
                memberTermRepository.save(newMemberTerm);
            }
        }
    }

    // 회원의 약관 동의 상태 조회 API
    @Transactional(readOnly = true)
    public List<MemberTermResponse> getMemberTermsStatus(Member member) {
        return memberTermRepository.findByMember(member)
                .stream()
                .map(MemberTermResponse::from)
                .toList();
    }
}
