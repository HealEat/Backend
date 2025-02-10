package healeat.server.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class StoreResonseDto {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreHomeDto {

        Long storeId;
        LocalDateTime createdAt;

        // 가게 공통 정보
        StoreInfoDto storeInfoDto;

        // Store 필요
        IsInDBDto isInDBDto;
        TotalStatDto totalStatDto;

        // Member 필요
        Long bookmarkId;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalStatDto {

        Float tastyScore;
        Float cleanScore;
        Float freshScore;
        Float nutrScore;
    }

    /**
     * 검색 - 가게 목록
     */
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
        SearchInfoDto searchInfoDto;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorePreviewDto {

        // 가게 공통 정보
        StoreInfoDto storeInfoDto;

        // 최근 사진 한 장
        ReviewResponseDto.ReviewImageDto reviewImageDto;

        /// Response 전용 필드
        Boolean isInDB;

        // Store 필요
        IsInDBDto isInDBDto;

        // Member 필요
        Long bookmarkId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsInDBDto {
        Float totalScore;
        Integer reviewCount;
        Float sickScore;
        Integer sickCount;
        Float vegetScore;
        Integer vegetCount;
        Float dietScore;
        Integer dietCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchInfoDto {
        // 홈 알고리즘에 사용
        String memberName;
        Boolean hasHealthInfo;

        // 현재 위치 또는 검색 지역명 위치
        String baseX;
        String baseY;
        Integer radius;

        String query;

        List<String> otherRegions;
        String selectedRegion;

        // 검색 시점의 api 호출 횟수
        Integer apiCallCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreInfoDto {
        Long placeId;
        String placeName;
        String categoryName;
        String phone;
        String addressName;
        String roadAddressName;
        String x;
        String y;
        String placeUrl;
        List<String> features;
    }
}
