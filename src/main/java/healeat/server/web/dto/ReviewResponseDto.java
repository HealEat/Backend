package healeat.server.web.dto;

import healeat.server.domain.ReviewImage;
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

        private Long deletedReviewId;
        private List<String> deletedReviewImageUrls;
        private LocalDateTime deletedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class myPageReviewListDto {

        private List<MyPageReviewDto> myPageReviewList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageReviewDto {

        private Long storeId;
        private String storeName;

        private ReviewPreviewDto reviewPreview;
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
        int imageCount;
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
        private ReviewerInfo reviewerInfo;

        private Long reviewId;
        private Float totalScore;
        private List<String> imageUrls;
        private String body;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewerInfo {

        private String name;
        private String profileImageUrl;

        // 작성 당시의 건강 목적
        private List<String> currentPurposes;
    }
}
