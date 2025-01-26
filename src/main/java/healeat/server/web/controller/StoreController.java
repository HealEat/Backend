package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.MemberRepository;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.ReviewRequestDto;
import healeat.server.web.dto.ReviewResponseDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;
    private final MemberRepository memberRepository;

    @Operation(summary = "특정 가게의 이미지 조회 API", description = "리뷰 이미지가 먼저 옵니다.")
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResonseDto.StoreImageListDto> getStoreImages(@PathVariable Long storeId) {
        /*StoreResonseDto.StoreImageListDto images =
                storeQueryServiceImpl.getReviewAndDaumImages(storeId);*/
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
}
