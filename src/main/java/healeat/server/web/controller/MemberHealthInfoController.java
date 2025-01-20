package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.web.dto.AnswerRequestDto;
import healeat.server.web.dto.AnswerResponseDto;
import healeat.server.web.dto.MemberHealthInfoResponseDto;
import healeat.server.web.dto.QuestionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberHealthInfoController {

    private final MemberHealthInfoService memberHealthInfoService;

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
