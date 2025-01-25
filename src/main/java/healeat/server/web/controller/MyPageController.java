package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.MemberHealQuestionConverter;
import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.service.MemberHealthInfoService;
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
    private final MemberHealthInfoService memberHealthInfoService;

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


    // 질환 정보 수정은 저장 API와 일치: 프론트에 알려주기


    @Operation(summary = "베지테리언 선택 변경과 계산 API", description = "베지테리언 선택을 업데이트하고," +
            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
    @PatchMapping("/health-info/veget")
    public ApiResponse<HealInfoResponseDto.ChoseResultDto> updateVegetarian(
            @AuthenticationPrincipal Member member,
            @RequestParam String vegetarian) {

        return ApiResponse.onSuccess(memberHealthInfoService.updateVegetarian(member, vegetarian));
    }

    @Operation(summary = "다이어트 선택 변경과 계산 API", description = "다이어트 선택을 업데이트하고," +
            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
    @PatchMapping("/health-info/diet")
    public ApiResponse<HealInfoResponseDto.ChoseResultDto> updateDiet(
            @AuthenticationPrincipal Member member,
            @RequestParam String diet) {

        return ApiResponse.onSuccess(memberHealthInfoService.updateDiet(member, diet));
    }

    @Operation(summary = "기본 질문의 답변 변경과 계산 API", description = "기본 질문의 답변을 업데이트하고," +
            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
    @PatchMapping("/health-info/{questionNum}")
    public ApiResponse<HealInfoResponseDto.BaseResultDto> updateAnswer(
            @AuthenticationPrincipal Member member,
            @PathVariable Integer questionNum,
            @RequestBody AnswerRequestDto request) {

        MemberHealQuestion memberHealQuestion = memberHealthInfoService.updateQuestion(member, questionNum, request);

        return ApiResponse.onSuccess(MemberHealQuestionConverter.toBaseQuestionDto(memberHealQuestion));
    }
}
