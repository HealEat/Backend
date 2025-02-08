package healeat.server.converter;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.web.dto.RecentSearchResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SearchPageConverter {

    public static RecentSearchResponseDto.DeleteResultDto toDeleteResultDto(RecentSearch recentSearch) {
        return RecentSearchResponseDto.DeleteResultDto.builder()
                .recentSearchId(recentSearch.getId())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static RecentSearchResponseDto.FoodCategoryResponseDto toFoodCategoryResponseDto(FoodCategory foodCategory) {

        return RecentSearchResponseDto.FoodCategoryResponseDto.builder()
                .categoryId(foodCategory.getId())
                .name(foodCategory.getName())
                .build();
    }

    public static RecentSearchResponseDto.FoodCategoryListResponseDto toFoodCategoryListResponseDto(List<FoodCategory> foodCategories) {

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<RecentSearchResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(SearchPageConverter::toFoodCategoryResponseDto)
                .toList();

        return RecentSearchResponseDto.FoodCategoryListResponseDto.builder()
                .FoodCategoryList(foodCategoryList)
                .build();
    }

    public static RecentSearchResponseDto.FoodFeatureListResponseDto toFoodFeatureListResponseDto(List<FoodFeature> foodFeatures) {

        //음식 특징 DTO 에 맞게 변환 후 리스트로 만들기
        List<RecentSearchResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(SearchPageConverter::toFoodFeatureResponseDto)
                .toList();

        return RecentSearchResponseDto.FoodFeatureListResponseDto.builder()
                .FoodFeatureList(foodFeatureList)
                .build();
    }

    public static RecentSearchResponseDto.FoodFeatureResponseDto toFoodFeatureResponseDto(FoodFeature foodFeature) {

        return RecentSearchResponseDto.FoodFeatureResponseDto.builder()
                .featureId(foodFeature.getId())
                .name(foodFeature.getName())
                .build();
    }

    public static RecentSearchResponseDto.RecentSearchDto toRecentSearchDto(RecentSearch recentSearch) {

        return RecentSearchResponseDto.RecentSearchDto.builder()
                .recentSearchId(recentSearch.getId())
                .searchType(recentSearch.getSearchType())
                .storeId(recentSearch.getStore().getId())
                .query(recentSearch.getQuery())
                .build();
    }
}
