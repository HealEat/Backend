package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.MemberRepository;
import healeat.server.service.BookmarkService;
import healeat.server.service.ReviewService;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.*;
import healeat.server.web.dto.api_response.DaumImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static healeat.server.converter.BookmarkConverter.toSetResponseDto;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final BookmarkService bookmarkService;

    /**
     * 가게 정보/이미지 조회
     */
    @Operation(summary = "가게 단건 조회 (이미지 제외)",
            description = "이미지를 제외한 모든 정보를 조회합니다.")
    @GetMapping("/{placeId}")
    public ApiResponse<StoreResonseDto.StoreHomeDto> getStoreDetails(
            @PathVariable Long placeId,
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(storeQueryServiceImpl.getStoreHome(placeId, testMember));
    }

    @Operation(summary = "가게 리뷰 이미지 조회",
            description = "가게 리뷰 이미지는 작성자의 정보를 포함하고, 최신순으로 페이징이 적용됩니다. 가게 이미지에 먼저 사용해주세요.")
    @GetMapping("/{placeId}/reviewImgs")
    public ApiResponse<ReviewResponseDto.ReviewImageDtoList> getStoreReviewImages(
            @PathVariable Long placeId) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "가게 Daum 이미지 조회",
            description = "가게 리뷰 이미지가 더이상 없으면 사용해주세요. 최대 15장입니다.")
    @GetMapping("/{placeId}/daumImgs")
    public ApiResponse<List<DaumImageResponseDto.Document>> getStoreDaumImages(
            @PathVariable Long placeId) {

        return ApiResponse.onSuccess(storeQueryServiceImpl.getStoreDaumImages(placeId));
    }

    /**
     * 가게 리뷰
     */
    @Operation(summary = "특정 가게의 리뷰 페이지 조회 API", description = "리뷰 리스트는 페이징을 포함합니다.")
    @GetMapping("/{placeId}/reviews")
    public ApiResponse<ReviewResponseDto.ReviewPreviewListDto> getReviewList(
            @PathVariable Long placeId,
            @CheckPage @RequestParam Integer page,
            @RequestParam SortBy sort,
            @RequestParam String sortOrder) {

        Page<Review> reviewPage = storeQueryServiceImpl.getReviewList(placeId, page, sort, sortOrder);
        return ApiResponse.onSuccess(ReviewConverter.toReviewPreviewListDto(reviewPage));
    }

    @Operation(summary = "특정 가게의 리뷰 작성 API", description = "isInDB가 ture인 " +
            "검색 결과에 대해서만 사용하는 API입니다.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/{placeId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewResponseDto.SetResultDto> createReview(
            @PathVariable Long placeId,
            @AuthenticationPrincipal Member member,
            @RequestPart(name = "files")
            List<MultipartFile> files,
            @RequestPart(name = "request", required = true)
            ReviewRequestDto request) {

        Member testMember = memberRepository.findById(999L).get();

        //로그인 연결되면 testMember만 나중에 member로 수정
        Review newReview = reviewService.createReview(placeId, testMember, files, request);
        return ApiResponse.onSuccess(ReviewConverter.toReviewSetResultDto(newReview));
    }

    /**
     * 가게 북마크
     */
    @Operation(summary = "가게 북마크 저장 API", description = "회원의 가게 북마크에 저장합니다." +
            " isInDB가 ture인 검색 결과에 대해서만 사용하는 API입니다.")
    @PostMapping("/{placeId}/bookmarks")
    public ApiResponse<BookmarkResponseDto> saveBookmark(
            @AuthenticationPrincipal Member member, @PathVariable Long placeId) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(toSetResponseDto(
                bookmarkService.saveBookmark(testMember, placeId)));
    }

    @Operation(summary = "가게 북마크 삭제 API", description = "회원의 가게 북마크에서 삭제합니다.")
    @DeleteMapping("/{placeId}/bookmarks/{bookmarkId}")
    public ApiResponse<BookmarkResponseDto> deleteBookmark(@PathVariable Long bookmarkId) {

        return ApiResponse.onSuccess(bookmarkService.deleteBookmark(bookmarkId));
    }
}