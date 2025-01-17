package healeat.server.converter;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.enums.SearchType;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.SearchPageResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchPageConverter {


    public SearchPageResponseDto.toDeleteResultDto toDeleteResultDto(RecentSearch recentSearch) {
        return SearchPageResponseDto.toDeleteResultDto.builder()
                .memberId(recentSearch.getMember().getId())
                .recentSearchId(recentSearch.getId())
                .searchType(recentSearch.getSearchType())
                .storeId(recentSearch.getStore().getId())
                .query(recentSearch.getQuery())
                .build();
    }

    public SearchPageResponseDto.FoodCategoryResponseDto toFoodCategoryResponseDto(FoodCategory foodCategory) {

        return SearchPageResponseDto.FoodCategoryResponseDto.builder()
                .categoryId(foodCategory.getId())
                .name(foodCategory.getName())
                .build();
    }

    public SearchPageResponseDto.FoodFeatureResponseDto toFoodFeatureResponseDto(FoodFeature foodFeature) {

        return SearchPageResponseDto.FoodFeatureResponseDto.builder()
                .featureId(foodFeature.getId())
                .name(foodFeature.getName())
                .build();
    }

    public SearchPageResponseDto.RecentSearchResponseDto toRecentSearchResponseDto(RecentSearch recentSearch) {

        return SearchPageResponseDto.RecentSearchResponseDto.builder()
                .recentSearchId(recentSearch.getId())
                .searchType(recentSearch.getSearchType())
                .storeId(recentSearch.getStore().getId())
                .query(recentSearch.getQuery())
                .build();
    }
}
