package healeat.server.web.dto;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.enums.SearchType;
import healeat.server.domain.mapping.RecentSearch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class SearchPageResponseDto {

    //검색창 - 음식 특징, 음식 종류, 최근 검색 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllSearchPageResponseDto {

        private List<FoodFeatureResponseDto> foodFeatureList;
        private List<FoodCategoryResponseDto> foodCategoryList;
        private List<RecentSearchResponseDto> recentSearchList;
    }

    //검색창 - 최근 검색 기록 삭제 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class toDeleteResultDto {

        private Long memberId;
        private Long recentSearchId;
        private SearchType searchType;
        private Long storeId;
        private String query;
    }

    //검색 키워드 보기 - 음식 종류 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodCategoryListResponseDto {

        private List<FoodCategoryResponseDto> FoodCategoryList;
    }
    //검색 키워드 보기 - 음식 종류 한 개 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodCategoryResponseDto {

        private Long categoryId;
        private String name;
    }

    //검색 키워드 보기 - 음식 종류 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodFeatureListResponseDto {

        private List<FoodFeatureResponseDto> FoodFeatureList;
    }

    //검색 키워드 보기 - 음식 특징 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodFeatureResponseDto {

        private Long featureId;
        private String name;
    }

    //검색창 - 최근 검색 리스트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentSearchListResponseDto {

        private List<RecentSearchResponseDto> recentSearchList;
    }

    //검색창 - 최근 검색
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentSearchResponseDto {

        private Long recentSearchId;
        private SearchType searchType;
        private Long storeId;
        private String query;
    }



}
