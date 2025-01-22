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
    public static class StorePreviewDtoList {


        // 가게 목록
        List<StorePreviewDto> storeList;

        // 페이징 관련
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;

        // 검색 정보
        SearchInfo searchInfo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchInfo {
        // 현재 위치 또는 검색 지역명 위치
        String baseX;
        String baseY;

        String query;
        String addedFilterFromQuery; // 쿼리로부터 추가된 필터
        List<String> otherRegions;
        String selectedRegion;

        // 검색 시점의 api 호출 횟수
        Integer apiCallCount;

        public void setApiCallCount(Integer apiCallCount) {
            this.apiCallCount = apiCallCount;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorePreviewDto {

        // 카카오 로컬 API
        Long id;
        String place_name;
        String category_name;
        String phone;
        String address_name;
        String road_address_name;
        String x;
        String y;
        String place_url; // 카카오맵으로 열기
        String distance;

        // 다음 이미지 API
        List<String> imageUrlList; // 한번에 받아와서, ..캐싱..

        // 힐릿 DB
        List<String> features; // 음식 특징
        Integer reviewCount; // 리뷰 수
        Float totalScore;
        Float sickScore; // 환자 점수
        Float vegetScore; // 베지테리언 점수
        Float dietScore; // 다이어터 점수
        Boolean isBookMarked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreImageListDto {

        // 리뷰 이미지 정보
        List<ReviewImagePreviewDto> reviewImagePreviewDtoList;
        // 다음 이미지 API
        List<String> daumImageUrls;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewImagePreviewDto {

        Long reviewId;
        ReviewResponseDto.ReviewerInfo reviewerInfo;
        String firstImageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeStorePreviewDto {

        String storeName;

        Float sickScore; // 환자 평점
        Integer sickCount; // 환자 리뷰 수

        Float vegetScore; // 베지테리언 평점
        Integer vegetCount; // 베지테리언 리뷰 수

        Float dietScore; // 다이어터 평점
        Integer dietCount; // 다이어터 리뷰 수

        List<String> images;

        List<HomeReviewPreviewDto> reviews; // 디자인에 나와있는 것처럼 리뷰 2개
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeReviewPreviewDto {

        String name; // reviewer info 로 대체될 예정

        Long reviewId;
        Float totalScore;
        String body;
        LocalDateTime createdAt;
    }
}
