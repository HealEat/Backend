package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.web.dto.AnswerRequestDto;
import healeat.server.web.dto.AnswerResponseDto;
import healeat.server.web.dto.MemberHealthInfoResponseDto;
import healeat.server.web.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberHealthInfoController {

    private final MemberHealthInfoService memberHealthInfoService;

    // 1. 사용자 앱 사용 목적과 질문 및 답변 전체 조회 api
    @GetMapping("/{memberId}/purposes")
    public ApiResponse<MemberHealthInfoResponseDto> getMemberHealthInfo(@PathVariable Long memberId) {
        return ApiResponse.onSuccess(memberHealthInfoService.getMemberHealthInfo(memberId));
    }

    // 2. 특정 질문 조회하기 api
    @GetMapping("/questions/{questionId}")
    public ApiResponse<QuestionResponseDto> getQuestion(@PathVariable Long questionId) {
        return ApiResponse.onSuccess(memberHealthInfoService.getQuestion(questionId));
    }

    // 3. 특정 질문 답변 저장하기 api
    @PutMapping("/{memberId}/questions/{questionId}/answers")
    public ApiResponse<AnswerResponseDto> saveAnswer(
            @PathVariable Long memberId,
            @PathVariable Long questionId,
            @RequestBody AnswerRequestDto request) {
        return ApiResponse.onSuccess(memberHealthInfoService.saveAnswer(memberId, questionId, request));
    }
}
