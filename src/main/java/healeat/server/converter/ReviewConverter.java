package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class ReviewConverter {

    public static ReviewResponseDto.ReviewPreviewDto toReviewPreviewDto(Review review) {

        Member member = review.getMember();
        ReviewResponseDto.ReviewerInfo reviewerInfo = ReviewResponseDto.ReviewerInfo.builder()
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .currentPurposes(review.getCurrentPurposes())
                .build();

        return ReviewResponseDto.ReviewPreviewDto.builder()
                .reviewerInfo(reviewerInfo)
                .reviewId(review.getId())
                .totalScore(review.getTotalScore())
                .imageUrls(/*review.getReviewImageList()*/ null) // 이미지 CRUD 구현 필요
                .body(review.getBody())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResponseDto.ReviewPreviewListDto toReviewPreviewListDto(Page<Review> reviewPage) {

        List<ReviewResponseDto.ReviewPreviewDto> reviewPreviewDtoList = reviewPage.stream()
                .map(ReviewConverter::toReviewPreviewDto)
                .toList();

        return ReviewResponseDto.ReviewPreviewListDto.builder()
                .reviewList(reviewPreviewDtoList)
                .listSize(reviewPreviewDtoList.size())
                .totalPage(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .isFirst(reviewPage.isFirst())
                .isLast(reviewPage.isLast())
                .build();
    }
}
