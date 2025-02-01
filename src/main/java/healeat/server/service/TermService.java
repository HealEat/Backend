package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.TermHandler;
import healeat.server.domain.Member;
import healeat.server.domain.Term;
import healeat.server.domain.mapping.MemberTerm;
import healeat.server.repository.MemberTermRepository;
import healeat.server.repository.TermRepository;
import healeat.server.web.dto.TermResponse;
import healeat.server.web.dto.TermsAgreeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;

    // 약관 목록 조회 API
    @Transactional(readOnly = true)
    public List<TermResponse> getTerms() {
        return termRepository.findAllOrderByIsRequired()
                .stream()
                .map(TermResponse::from)
                .collect(Collectors.toList());
    }

    // 회원의 약관 동의 저장 API
    @Transactional
    public void saveUserAgreement(Member member, TermsAgreeRequest request) {
        for (TermsAgreeRequest.TermAgree agree : request.getAgreements()) {
            Term term = termRepository.findById(agree.getTermId())
                    .orElseThrow(() -> new TermHandler(ErrorStatus.INVALID_TERM_ID));

            memberTermRepository.save(
                    MemberTerm.builder()
                            .member(Member.builder().id(member.getId()).build())
                            .term(term)
                            .agree(agree.getAgree())
                            .build()
            );
        }
    }
}
