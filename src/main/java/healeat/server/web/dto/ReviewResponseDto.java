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
        LocalDateTime deletedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class myPageReviewListDto {

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

        Long storeId;
        String storeName;
        String storeImage;
        String storeCategory;
        Float storeTotalScore;

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
}
