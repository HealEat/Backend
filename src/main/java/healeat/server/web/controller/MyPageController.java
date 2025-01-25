package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.HealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final HealthInfoService healthInfoService;

    @Operation(summary = "프로필 정보 조회 API", description = "프로필 수정 화면에 사용합니다.")
    @GetMapping("/profile")
    public ApiResponse<MemberProfileResponseDto> getProfileInfo(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(memberService.getProfileInfo(member));
    }

    @Operation(summary = "프로필 수정 API")
    @PatchMapping("/profile")
    public ApiResponse<MemberProfileResponseDto> updateProfile(
            @AuthenticationPrincipal Member member, @RequestBody MemberProfileRequestDto request) {
        return ApiResponse.onSuccess(memberService.updateProfile(member, request));
    }

    @Operation(summary = "내가 남긴 후기 목록 조회 API")
    @GetMapping("/reviews")
    public ApiResponse<ReviewResponseDto.myPageReviewListDto> getMyReviews(@AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "특정 리뷰 삭제 API")
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<ReviewResponseDto.DeleteResultDto> deleteReview(
            @AuthenticationPrincipal Member member, @PathVariable Long reviewId) {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "마이페이지 나의 건강정보 조회 API",
            description = "마이페이지에서 나의 건강정보를 전체 조회할 수 있습니다.")
    @GetMapping("/health-info")
    public ApiResponse<MemberProfileResponseDto.MyHealthProfileDto> getMyHealthInfo(
            @AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "특정 질문 답변 업데이트하기 API")
    @PostMapping("/health-info/{questionNum}")
    public ApiResponse<AnswerResponseDto> saveAnswer(
            @AuthenticationPrincipal Member member,
            @PathVariable Integer questionNum,
            @RequestBody AnswerRequestDto request) {

        return ApiResponse.onSuccess(
                healthInfoService.updateQuestion(member, questionNum, request));
    }
}
