package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        List<String> otherRegions;
        String selectedRegion;

        // 런칭 때 지우기
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
}
