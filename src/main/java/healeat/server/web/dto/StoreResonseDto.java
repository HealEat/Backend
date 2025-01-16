package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class StoreResonseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorePreviewListDto {

        List<StorePreviewDto> storeList;
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
    public static class StorePreviewDto {

        Long storeId;
        String storeName;
        String category; // 음식 종류
        List<String> features; // 음식 특징

        Integer reviewCount; // 리뷰 수

        Float totalScore;
        Float sickScore; // 환자 점수
        Float vegetScore; // 베지테리언 점수
        Float dietScore; // 다이어터 점수

        Boolean isBookMarked;
        String kakaoMapUrl; // 카카오맵으로 열기
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

        String name;
        List<String> currentPurposes;

        Long reviewId;
        Float totalScore;
        List<String> images;
        String body;
        LocalDateTime createdAt;
    }
}
