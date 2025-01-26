package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.MemberRepository;
import healeat.server.service.BookmarkService;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static healeat.server.converter.BookmarkConverter.toSetResponseDto;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;
    private final MemberRepository memberRepository;
    private final BookmarkService bookmarkService;

    @Operation(summary = "특정 가게의 세부정보(또는 이미지까지) 조회 API",
            description = "필수 세부정보 : 맛-청결-신선-영양 점수")
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResonseDto.StoreImageListDto> getStoreDetails(@PathVariable Long storeId) {
        /*StoreResonseDto.StoreImageListDto images =
                storeQueryServiceImpl.getReviewAndDaumImages(storeId);*/
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "가게 저장 API", description = "가게를 DB에 저장하는 Trigger 주목: " +
            "isInDB(response 필드)가 false인 가게를 대상으로 리뷰를 작성, 또는 회원의 북마크에 저장")
    @PostMapping("/{storeId}")
    public ApiResponse<StoreResonseDto.SetResultDto> addStore(
            @RequestBody StoreRequestDto.ForSaveStoreDto request) {

        return ApiResponse.onSuccess(StoreConverter.toSetResultDto(
                storeQueryServiceImpl.saveStore(request)));
    }

    @Operation(summary = "특정 가게의 리뷰 작성 API")
    @PostMapping("/{storeId}/reviews")
    public ApiResponse<ReviewResponseDto.SetResultDto> createReview(
            @PathVariable Long storeId,
            @AuthenticationPrincipal Member member,
            @ModelAttribute ReviewRequestDto request) {

        Member testMember = memberRepository.findById(999L).get();

//        ReviewResponseDto.SetResultDto newReview = storeQueryServiceImpl.createReview(storeId, member);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "특정 가게의 리뷰 페이지 조회 API", description = "리뷰 리스트는 페이징을 포함합니다.")
    @GetMapping("/{storeId}/reviews")
    public ApiResponse<ReviewResponseDto.ReviewPreviewListDto> getReviewList(
            @PathVariable Long storeId,
            @CheckPage @RequestParam Integer page,
            @RequestParam SortBy sort,
            @RequestParam String sortOrder) {
        Page<Review> reviewPage = storeQueryServiceImpl.getReviewList(storeId, page, sort, sortOrder);
        return ApiResponse.onSuccess(ReviewConverter.toReviewPreviewListDto(reviewPage));
    }

    @Operation(summary = "가게 북마크 저장 API", description = "회원의 가게 북마크에 저장합니다." +
            " 만약, DB에 존재하지 않았던 가게에 북마크를 하면 isNewStore 진리값을 true로 반환합니다.")
    @PostMapping("/{storeId}/bookmarks")
    public ApiResponse<BookmarkResponseDto> saveBookmark(
            @AuthenticationPrincipal Member member, @PathVariable Long storeId) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(toSetResponseDto(
                bookmarkService.saveBookmark(testMember, storeId)));
    }

    @Operation(summary = "가게 북마크 삭제 API", description = "회원의 가게 북마크에서 삭제합니다.")
    @DeleteMapping("/{storeId}/bookmarks/{bookmarkId}")
    public ApiResponse<BookmarkResponseDto> deleteBookmark(@PathVariable Long bookmarkId) {

        return ApiResponse.onSuccess(bookmarkService.deleteBookmark(bookmarkId));
    }
}
