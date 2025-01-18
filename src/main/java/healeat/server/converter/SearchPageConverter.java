package healeat.server.converter;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.web.dto.SearchPageResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchPageConverter {

    public static SearchPageResponseDto.toDeleteResultDto toDeleteResultDto(RecentSearch recentSearch) {
        return SearchPageResponseDto.toDeleteResultDto.builder()
                .memberId(recentSearch.getMember().getId())
                .recentSearchId(recentSearch.getId())
                .searchType(recentSearch.getSearchType())
                .storeId(recentSearch.getStore().getId())
                .query(recentSearch.getQuery())
                .build();
    }

    public static SearchPageResponseDto.FoodCategoryResponseDto toFoodCategoryResponseDto(FoodCategory foodCategory) {

        return SearchPageResponseDto.FoodCategoryResponseDto.builder()
                .categoryId(foodCategory.getId())
                .name(foodCategory.getName())
                .build();
    }

    public static SearchPageResponseDto.FoodCategoryListResponseDto toFoodCategoryListResponseDto(List<FoodCategory> foodCategories) {

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(SearchPageConverter::toFoodCategoryResponseDto)
                .toList();

        return SearchPageResponseDto.FoodCategoryListResponseDto.builder()
                .FoodCategoryList(foodCategoryList)
                .build();
    }

    public static SearchPageResponseDto.FoodFeatureListResponseDto toFoodFeatureListResponseDto(List<FoodFeature> foodFeatures) {

        //음식 특징 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(SearchPageConverter::toFoodFeatureResponseDto)
                .toList();

        return SearchPageResponseDto.FoodFeatureListResponseDto.builder()
                .FoodFeatureList(foodFeatureList)
                .build();
    }

    public static SearchPageResponseDto.FoodFeatureResponseDto toFoodFeatureResponseDto(FoodFeature foodFeature) {

        return SearchPageResponseDto.FoodFeatureResponseDto.builder()
                .featureId(foodFeature.getId())
                .name(foodFeature.getName())
                .build();
    }

    public static SearchPageResponseDto.RecentSearchResponseDto toRecentSearchResponseDto(RecentSearch recentSearch) {

        return SearchPageResponseDto.RecentSearchResponseDto.builder()
                .recentSearchId(recentSearch.getId())
                .searchType(recentSearch.getSearchType())
                .storeId(recentSearch.getStore().getId())
                .query(recentSearch.getQuery())
                .build();
    }
}
