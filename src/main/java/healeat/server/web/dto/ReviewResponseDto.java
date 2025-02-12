package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponseDto {

    /**
     * 마이페이지
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultDto {

        Long deletedReviewId;
        List<String> deletedReviewImageUrls;
        LocalDateTime deletedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageReviewListDto {

        List<MyPageReviewDto> myPageReviewList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageReviewDto {

        Long placeId;
        String placeName;

        ReviewPreviewDto reviewPreview;
    }

    /**
     * 가게 리뷰 페이지
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResultDto {

        Long reviewId;
        Integer imageCount;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewPreviewListDto {

        List<ReviewPreviewDto> reviewList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewPreviewDto {

        // 리뷰 작성자 정보
        ReviewerInfo reviewerInfo;

        Long reviewId;
        Float totalScore;
        List<String> imageUrls;
        String body;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewerInfo {

        String name;
        String profileImageUrl;

        // 작성 당시의 건강 목적
        List<String> currentPurposes;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewImageDtoList {

        List<ReviewImageDto> reviewImageDtoList;

        // 페이징 관련
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewImageDto {

        Long reviewId;
        String imageUrl;
        ReviewerInfo reviewerInfo;
    }
}
