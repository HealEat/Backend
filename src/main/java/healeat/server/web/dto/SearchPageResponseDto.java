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

@Getter
@Builder
public class SearchPageResponseDto {

        List<FoodFeatureResponseDto> foodFeatureList;
        List<FoodCategoryResponseDto> foodCategoryList;
        List<RecentSearchResponseDto> recentSearchList;

    //검색창 - 최근 검색 기록 삭제 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class toDeleteResultDto {

        Long memberId;
        Long recentSearchId;
        SearchType searchType;
        Long storeId;
        String query;
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

//    //검색창 - 최근 검색 리스트 DTO - 나중에 따로 필요하면 쓸듯
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class RecentSearchListResponseDto {
//
//        private List<RecentSearchResponseDto> recentSearchList;
//    }

    //검색창 - 최근 검색
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentSearchResponseDto {

        Long recentSearchId;
        SearchType searchType;
        Long storeId;
        String query;
    }



}
