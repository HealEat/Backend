package healeat.server.web.dto;

import healeat.server.domain.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecentSearchResponseDto {

    List<RecentSearchDto> recentSearchList;
    Integer listSize;
    Integer totalPage;
    Long totalElements;
    Boolean isFirst;
    Boolean isLast;

    //검색창 - 최근 검색 기록 삭제 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultDto {

        Long recentSearchId;
        LocalDateTime deletedAt;
    }

    //검색 키워드 보기 - 음식 종류 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodCategoryListResponseDto {

        List<FoodCategoryResponseDto> FoodCategoryList;
    }
    //검색 키워드 보기 - 음식 종류 한 개 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodCategoryResponseDto {

        Long categoryId;
        String name;
    }

    //검색 키워드 보기 - 음식 종류 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodFeatureListResponseDto {

        List<FoodFeatureResponseDto> FoodFeatureList;
    }

    //검색 키워드 보기 - 음식 특징 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodFeatureResponseDto {

        Long featureId;
        String name;
    }

    //검색창 - 최근 검색
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentSearchDto {

        Long recentSearchId;
        SearchType searchType;
        Long placeId;
        String query;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResultDto {

        Long recentSearchId;
        LocalDateTime createdAt;
    }
}
