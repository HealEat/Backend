package healeat.server.service;

import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.web.dto.SearchPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchPageService {

    private final CategoryFeatureService categoryFeatureService;
    private final RecentSearchService recentSearchService;

    public SearchPageResponseDto getAllSearchPage() {

        //음식 종류, 음식 특징, 최근 검색 리스트 모두 가져오기
        List<FoodCategory> foodCategories = categoryFeatureService.getAllFoodCategories();
        List<FoodFeature> foodFeatures = categoryFeatureService.getAllFoodFeatures();
        List<RecentSearch> recentSearches = recentSearchService.getAllRecentSearches();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(SearchPageConverter::toFoodCategoryResponseDto)
                .toList();

        //음식 특징 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(SearchPageConverter::toFoodFeatureResponseDto)
                .toList();

        //최근 검색 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.RecentSearchResponseDto> recentSearchList = recentSearches.stream()
                .map(SearchPageConverter::toRecentSearchResponseDto)
                .collect(Collectors.toList());


        return SearchPageResponseDto.builder()
                .foodCategoryList(foodCategoryList)
                .foodFeatureList(foodFeatureList)
                .recentSearchList(recentSearchList)
                .build();
    }
}
