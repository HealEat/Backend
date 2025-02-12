package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.MemberRepository;
import healeat.server.service.BookmarkService;
import healeat.server.service.ReviewService;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.*;
import healeat.server.web.dto.apiResponse.DaumImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static healeat.server.converter.BookmarkConverter.toSetResponseDto;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Validated
public class StoreController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final BookmarkService bookmarkService;

    /**
     * 가게 정보/이미지 조회
     */
    @Operation(summary = "가게 단건 조회 (이미지 제외) API",
            description = "이미지를 제외한 모든 정보를 조회합니다.")
    @GetMapping("/{placeId}")
    public ApiResponse<StoreResponseDto.StoreHomeDto> getStoreDetails(
            @PathVariable Long placeId,
            @AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(storeQueryServiceImpl.getStoreHome(placeId, member));
    }

    @Operation(summary = "가게 리뷰 이미지 조회 API", description =
            """
                    가게의 리뷰 이미지 전체를 조회하며, 이미지마다 작성자의 정보도 포함합니다.
                    가게 이미지 조회 시 '먼저' 사용해주세요.
                    
                    페이징이 적용됩니다.(페이지 당 10개)""")
    @GetMapping("/{placeId}/reviewImgs")
    public ApiResponse<ReviewResponseDto.ReviewImageDtoList> getStoreReviewImages(
            @PathVariable Long placeId,
            @CheckPage @RequestParam Integer page) {

        Page<ReviewImage> reviewImagePage = reviewService.getStoreReviewImages(placeId, page);

        return ApiResponse.onSuccess(ReviewConverter.toReviewImageDtoList(reviewImagePage));
    }

    @Operation(summary = "가게 Daum 이미지 조회 API",
            description = "가게 이미지 조회 시 리뷰 이미지 조회 API에 이어서 사용해주세요. 최대 15장입니다.")
    @GetMapping("/{placeId}/daumImgs")
    public ApiResponse<List<DaumImageResponseDto.Document>> getStoreDaumImages(
            @PathVariable Long placeId) {

        return ApiResponse.onSuccess(storeQueryServiceImpl.getStoreDaumImages(placeId));
    }

    /**
     * 가게 리뷰
     */
    @Operation(summary = "특정 가게의 리뷰 페이지 조회 API", description =
            """
                    Request Body에
                    1. 동적 정렬 기준 : LATEST(기본) / DESC(높은 순) / ASC(낮은 순)
                    2. 필터 조건 : 리스트 - [SICK, VEGET, DIET] (기본은 전부) 를 받아서 리뷰 목록을 조회합니다.
                    
                    페이징이 적용됩니다.(페이지 당 10개)""")
    @GetMapping("/{placeId}/reviews")
    public ApiResponse<ReviewResponseDto.ReviewPreviewListDto> getReviewList(
            @PathVariable Long placeId,
            @CheckPage @RequestParam Integer page,
            @RequestParam(required = false, defaultValue = "LATEST") String sortBy,
            // filters[] 형식도 지원
            @RequestParam(name = "filters[]", required = false) List<String> filters) {

        Page<Review> reviewPage = reviewService.getStoreReviews(placeId, page, sortBy, filters);
        return ApiResponse.onSuccess(ReviewConverter.toReviewPreviewListDto(reviewPage));
    }

    @Operation(summary = "특정 가게의 리뷰 작성 API",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/{placeId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReviewResponseDto.SetResultDto> createReview(
            @PathVariable Long placeId,
            @AuthenticationPrincipal Member member,
            @RequestPart(name = "files", required = false)
            List<MultipartFile> files,
            @Valid
            @RequestPart(name = "request", required = true)
            ReviewRequestDto request) {

        if(member == null) {
            return ApiResponse.onFailure("UNAUTHORIZED", "로그인이 필요합니다.");
        }

        Review newReview = reviewService.createReview(placeId, member, files, request);
        return ApiResponse.onSuccess(ReviewConverter.toReviewSetResultDto(newReview));
    }

    /**
     * 가게 북마크
     */
    @Operation(summary = "가게 북마크 저장 API", description = "회원의 가게 북마크로 저장합니다.")
    @PostMapping("/{placeId}/bookmarks")
    public ApiResponse<BookmarkResponseDto> saveBookmark(
            @AuthenticationPrincipal Member member, @PathVariable Long placeId) {

        if (member == null) {
            return ApiResponse.onFailure("UNAUTHORIZED", "로그인이 필요합니다.");
        }

        return ApiResponse.onSuccess(toSetResponseDto(
                bookmarkService.saveBookmark(member, placeId)));
    }

    @Operation(summary = "가게 북마크 삭제 API", description = "회원의 가게 북마크에서 삭제합니다.")
    @DeleteMapping("/{placeId}/bookmarks/{bookmarkId}")
    public ApiResponse<BookmarkResponseDto> deleteBookmark(@PathVariable Long bookmarkId) {

        return ApiResponse.onSuccess(bookmarkService.deleteBookmark(bookmarkId));
    }
}