package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.enums.Purpose;
import healeat.server.domain.mapping.Review;
import healeat.server.service.StoreQueryService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreQueryService storeQueryService;

    @GetMapping("/{storeId}/reviews")
    @Operation(summary = "특정 가게의 리뷰 목록 조회 API",
            description = "특정 가게의 리뷰들의 목록을 조회하는 API이며, 페이징을 포함합니다." +
                    "sort는 null일 때 최신 순(기본값)")
    public ApiResponse<StoreResonseDto.ReviewPreviewListDto> getReviewList(
            @PathVariable Long storeId,
            @CheckPage @RequestParam Integer page,
            @RequestParam Purpose sort,
            @RequestParam String sortOrder) {
        Page<Review> reviewPage = storeQueryService.getReviewList(storeId, page, sort, sortOrder);
        return ApiResponse.onSuccess(ReviewConverter.toReviewPreviewListDto(reviewPage));
    }
}
