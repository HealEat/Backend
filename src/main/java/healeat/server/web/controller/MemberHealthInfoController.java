package healeat.server.web.controller;

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

    // 사용자 앱 사용 목적과 질문 및 답변 전체 조회 api
    @GetMapping("/{memberId}/purposes")
    public ResponseEntity<MemberHealthInfoResponseDto> getMemberHealthInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberHealthInfoService.getMemberHealthInfo(memberId));
    }

    // 특정 질문 조회하기 api
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponseDto> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(memberHealthInfoService.getQuestion(questionId));
    }

    // 특정 질문 답변 저장하기 api
    @PutMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerResponseDto> saveAnswer(@PathVariable Long questionId,@RequestBody AnswerRequestDto request) {
        return ResponseEntity.ok(memberHealthInfoService.saveAnswer(questionId, request));
    }

    // 회원 건강 정보 답변 수정 기능 api
    @PutMapping("/{memberId}/questions/{questionId}")
    public ResponseEntity<AnswerResponseDto> updateAnswer(
            @PathVariable Long memberId,
            @PathVariable Long questionId,
            @RequestBody AnswerRequestDto request) {
        return ResponseEntity.ok(memberHealthInfoService.updateAnswer(memberId, questionId, request));
    }
}
