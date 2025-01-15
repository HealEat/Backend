package healeat.server.converter;

import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class ReviewConverter {

    public static StoreResonseDto.ReviewPreviewDto toReviewPreviewDto(Review review) {
        return StoreResonseDto.ReviewPreviewDto.builder()
                .name(review.getMember().getName())
                .currentHealthGoal(review.getCurrentHealthGoal())
                .reviewId(review.getId())
                .totalScore(review.getTotalScore())
                .images(/*review.getReviewImageList()*/ null) // 이미지 CRUD 구현 필요
                .body(review.getBody())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static StoreResonseDto.ReviewPreviewListDto toReviewPreviewListDto(Page<Review> reviewPage) {

        List<StoreResonseDto.ReviewPreviewDto> reviewPreviewDtoList = reviewPage.stream()
                .map(ReviewConverter::toReviewPreviewDto)
                .toList();

        return StoreResonseDto.ReviewPreviewListDto.builder()
                .reviewList(reviewPreviewDtoList)
                .listSize(reviewPreviewDtoList.size())
                .totalPage(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .isFirst(reviewPage.isFirst())
                .isLast(reviewPage.isLast())
                .build();
    }
}
