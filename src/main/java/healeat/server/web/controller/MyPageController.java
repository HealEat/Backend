package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.MemberService;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import healeat.server.web.dto.ReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @Operation(summary = "프로필 정보 조회 API", description = "프로필 수정 화면에 사용합니다.")
    @GetMapping
    public ApiResponse<MemberProfileResponseDto> getProfileInfo(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(memberService.getProfileInfo(member));
    }

    @Operation(summary = "프로필 수정 API")
    @PatchMapping
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

    @GetMapping("/health-info")
    @Operation(summary = "마이페이지 나의 건강정보 조회 API",
            description = "마이페이지에서 나의 건강정보를 전체 조회할 수 있습니다.")
    public ApiResponse<MemberProfileResponseDto.MyHealthProfileDto> getMyHealthInfo(
            @AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(null);
    }
}
