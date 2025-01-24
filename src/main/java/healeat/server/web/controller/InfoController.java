package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final MemberService memberService;
    private final MemberHealthInfoService memberHealthInfoService;

    @Operation(summary = "프로필 설정 API")
    @PostMapping
    public ApiResponse<MemberProfileResponseDto> createProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberProfileRequestDto request) {

        // create, update, delete(이미지가 null로 들어온다든지), 동일 이름 중복 .. 의 로직을 통합해서
        // createOrUpdate(member, request) <- 등의 메서드를 서비스 단에 개발하는 것 추천
        // 모두 POST로 묶으면 됨. POST는 무적
        return ApiResponse.onSuccess(/*memberService.createProfile(member, request)*/null);
    }

    @Operation(summary = "사용자 앱 사용 목적과 질문 및 답변 전체 조회 API")
    @GetMapping("/purposes")
    public ApiResponse<MemberHealthInfoResponseDto> getMemberHealthInfo(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(memberHealthInfoService.getMemberHealthInfo(member.getId()));
    }

    @Operation(summary = "특정 질문 조회하기 API")
    @GetMapping("/questions/{questionId}")
    public ApiResponse<QuestionResponseDto> getQuestion(@PathVariable Integer questionId) {
        return ApiResponse.onSuccess(memberHealthInfoService.getQuestion(questionId));
    }

    @Operation(summary = "특정 질문 답변 저장하기 API")
    @PutMapping("/questions/{questionId}/answers")
    public ApiResponse<AnswerResponseDto> saveAnswer(
            @AuthenticationPrincipal Member member,
            @PathVariable Integer questionId,
            @RequestBody AnswerRequestDto request) {
        return ApiResponse.onSuccess(memberHealthInfoService.saveAnswer(member.getId(), questionId, request));
    }
}
