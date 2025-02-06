package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.ImageResponseDto;
import healeat.server.web.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                .imageUrls(review.getReviewImageList().stream()
                        .map(ReviewImage::getFilePath)
                        .collect(Collectors.toList())) // 이미지 CRUD 구현 필요
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

    public static ImageResponseDto.publicUrlDto toReviewImage(ReviewImage reviewImage) {

        return ImageResponseDto.publicUrlDto.builder()
                .id(reviewImage.getId())
                .imageUrl(reviewImage.getFilePath())
                .build();
    }

    public static ReviewResponseDto.SetResultDto toReviewSetResultDto(Review review) {

        return ReviewResponseDto.SetResultDto.builder()
                .reviewId(review.getId())
                .imageCount(review.getReviewImageList().size())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResponseDto.DeleteResultDto toReviewDeleteResultDto(Review review) {
        return ReviewResponseDto.DeleteResultDto.builder()
                .deletedReviewId(review.getId())
                .deletedReviewImageUrls(review.getReviewImageList().stream()
                        .map(ReviewImage::getFilePath)
                        .collect(Collectors.toList()))
                .deletedAt(LocalDateTime.now())
                .build();
    }


}
