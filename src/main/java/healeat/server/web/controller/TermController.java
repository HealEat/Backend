package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.TermService;
import healeat.server.web.dto.MemberTermResponse;
import healeat.server.web.dto.TermResponse;
import healeat.server.web.dto.TermsAgreeRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;
    private final MemberRepository memberRepository;

    @Operation(summary = "약관 조회 API")
    @GetMapping
    public ApiResponse<List<TermResponse>> getTerms() {
        return ApiResponse.onSuccess(termService.getTerms());
    }

    @Operation(summary = "사용자 약관 동의 받기 API", description = "서버에서 테스트 멤버를 주입하지 않습니다. " +
            "실제 로그인 시의 멤버 토큰을 주입해주세요.")
    @PostMapping("/agree")
    public ApiResponse<Void> agreeToTerms(
            @AuthenticationPrincipal Member member,
            @RequestBody @Valid TermsAgreeRequest request) {

        Member testMember = memberRepository.findById(999L).get();

        termService.saveMemberAgreement(testMember, request);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "사용자의 약관 동의 상태 조회 API")
    @GetMapping("/status")
    public ApiResponse<List<MemberTermResponse>> getTermStatus(
            @AuthenticationPrincipal Member member
    ) {
        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(termService.getMemberTermsStatus(testMember));
    }
}
