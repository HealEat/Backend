package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import healeat.server.service.BookmarkService;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.service.ReviewService;
import healeat.server.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")  // 로그인 안된 사용자가 접근하면 403 Forbidden 반환
public class MyPageController {

    private final MemberService memberService;
    private final MemberHealthInfoService memberHealthInfoService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "프로필 정보 조회 API", description = "프로필 수정 화면에 사용합니다.")
    @GetMapping("/profile")
    public ApiResponse<MemberProfileResponseDto> getProfileInfo(@AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(memberService.getProfileInfo(member));
    }

    @Operation(summary = "프로필 수정 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberProfileResponseDto> updateProfile(
            @AuthenticationPrincipal Member member,
            @RequestPart(name = "file", required = false)
            MultipartFile file,
            @RequestPart(name = "request", required = true)
            MemberProfileRequestDto request) {

        Member changedProfileMember = memberService.updateProfile(member, file, request);
        return ApiResponse.onSuccess(MemberProfileResponseDto.from(changedProfileMember));
    }

    @Operation(summary = "내가 남긴 후기 목록 조회 API",
            description = "내가 작성한 후기 목록을 페이징 처리하여 조회")
    @GetMapping("/reviews")
    public ApiResponse<ReviewResponseDto.MyPageReviewListDto> getMyReviews(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        ReviewResponseDto.MyPageReviewListDto responseDto = reviewService.getMyReviews(member, pageable);

        return ApiResponse.onSuccess(responseDto);
    }

    @Operation(summary = "특정 리뷰 삭제 API")
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<ReviewResponseDto.DeleteResultDto> deleteReview(
            @AuthenticationPrincipal Member member,
            @PathVariable Long reviewId) {

        Review deleteReview = reviewService.deleteReview(member, reviewId);
        return ApiResponse.onSuccess(ReviewConverter.toReviewDeleteResultDto(deleteReview));
    }

    @Operation(summary = "마이페이지 나의 건강정보 조회 API",
            description = "마이페이지에서 나의 건강정보를 전체 조회할 수 있습니다.")
    @GetMapping("/health-info")
    public ApiResponse<HealInfoResponseDto.MyHealthInfoDto> getMyHealthInfo(
            @AuthenticationPrincipal Member member) {

        HealInfoResponseDto.MyHealthInfoDto responseDto = memberHealthInfoService.getMyHealthInfo(member);
        return ApiResponse.onSuccess(responseDto);
    }

    @Operation(summary = "회원의 북마크 목록 조회 API", description = "회원이 저장한 북마크 목록을 조회합니다.")
    @GetMapping("/bookmarks")
    public ApiResponse<StoreResponseDto.StorePreviewDtoList> getMemberBookmarks(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        if (member == null) {
            return ApiResponse.onFailure("UNAUTHORIZED", "로그인이 필요합니다.");
        }
        return ApiResponse.onSuccess(bookmarkService.getMemberBookmarks(member, page, size));
    }

    // 기획안 수정됨 : 건강 정보 각각에 대한 정보 수정 X -> 건강 정보 전체에 대한 정보 수정 O (프론트에서 Info 도메인 재사용한다고 함)

//
//
//    // 질환 정보 수정은 저장 API와 일치: 프론트에 알려주기
//
//
//    @Operation(summary = "베지테리언 선택 변경과 계산 API", description = "베지테리언 선택을 업데이트하고," +
//            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
//    @PatchMapping("/health-info/veget")
//    public ApiResponse<ChangeChoiceResultDto> updateVegetarian(
//            @AuthenticationPrincipal Member member,
//            @RequestParam String vegetarian) {
//
//        Member testMember = memberRepository.findById(999L).get();
//
//        return ApiResponse.onSuccess(toChangeVegetResult(
//                memberHealthInfoService.updateVegetarian(testMember, vegetarian)));
//    }
//
//    @Operation(summary = "다이어트 선택 변경과 계산 API", description = "다이어트 선택을 업데이트하고," +
//            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
//    @PatchMapping("/health-info/diet")
//    public ApiResponse<ChangeChoiceResultDto> updateDiet(
//            @AuthenticationPrincipal Member member,
//            @RequestParam String diet) {
//
//        Member testMember = memberRepository.findById(999L).get();
//
//        return ApiResponse.onSuccess(toChangeDietResult(
//                memberHealthInfoService.updateDiet(testMember, diet)));
//    }
//
//    @Operation(summary = "기본 질문의 답변 변경과 계산 API", description = "기본 질문의 답변을 업데이트하고," +
//            "멤버의 새로운 healEatFoods(추천 음식 카테고리 리스트)를 계산 후 수정합니다.")
//    @PatchMapping("/health-info/{questionNum}")
//    public ApiResponse<ChangeBaseResultDto> updateAnswer(
//            @AuthenticationPrincipal Member member,
//            @PathVariable Integer questionNum,
//            @RequestBody AnswerRequestDto request) {
//
//        Member testMember = memberRepository.findById(999L).get();
//
//        MemberHealQuestion memberHealQuestion = memberHealthInfoService
//                .updateQuestion(testMember, questionNum, request);
//
//        return ApiResponse.onSuccess(toChangeBaseResult(memberHealQuestion));
//    }
}
