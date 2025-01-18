package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.service.FoodCategoryService;
import healeat.server.service.FoodFeatureService;
import healeat.server.service.RecentSearchService;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.SearchPageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/searchPage")
@RequiredArgsConstructor
public class SearchPageController {

    private final RecentSearchService recentSearchService;
    private final FoodCategoryService foodCategoryService;
    private final FoodFeatureService foodFeatureService;
    private final SearchPageConverter searchPageConverter;

    @Operation(summary = "검색창 구현", description = "검색창을 조회합니다.(음식 종류, 음식 특징, 최근 검색 목록 포함된 페이지)")
    @GetMapping
    public ApiResponse<SearchPageResponseDto.AllSearchPageResponseDto> getAllRecentSearches() {

        //음식 종류, 음식 특징, 최근 검색 리스트 모두 가져오기
        List<FoodCategory> foodCategories = foodCategoryService.getAllFoodCategories();
        List<FoodFeature> foodFeatures = foodFeatureService.getAllFoodFeatures();
        List<RecentSearch> recentSearches = recentSearchService.getAllRecentSearches();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(searchPageConverter::toFoodCategoryResponseDto)
                .toList();

        //음식 특징 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(searchPageConverter::toFoodFeatureResponseDto)
                .toList();

        //최근 검색 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.RecentSearchResponseDto> recentSearchList = recentSearches.stream()
                .map(searchPageConverter::toRecentSearchResponseDto)
                .collect(Collectors.toList());


        SearchPageResponseDto.AllSearchPageResponseDto response = SearchPageResponseDto.AllSearchPageResponseDto.builder()
                .foodCategoryList(foodCategoryList)
                .foodFeatureList(foodFeatureList)
                .recentSearchList(recentSearchList)
                .build();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "음식 종류 조회", description = "음식 종류를 전체 조회합니다.")
    @GetMapping("/foodCategory")
    public ApiResponse<SearchPageResponseDto.FoodCategoryListResponseDto> getFoodCategoryLists(){

        List<FoodCategory> foodCategories = foodCategoryService.getAllFoodCategories();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodCategoryResponseDto> foodCategoryList = foodCategories.stream()
                .map(searchPageConverter::toFoodCategoryResponseDto)
                .toList();

        SearchPageResponseDto.FoodCategoryListResponseDto response = SearchPageResponseDto.FoodCategoryListResponseDto.builder()
                .FoodCategoryList(foodCategoryList)
                .build();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "음식 특징 조회", description = "음식 특징을 전체 조회합니다.")
    @GetMapping("/foodFeature")
    public ApiResponse<SearchPageResponseDto.FoodFeatureListResponseDto> getFoodFeatureLists(){

        List<FoodFeature> foodFeatures = foodFeatureService.getAllFoodFeatures();

        //음식 종류 DTO 에 맞게 변환 후 리스트로 만들기
        List<SearchPageResponseDto.FoodFeatureResponseDto> foodFeatureList = foodFeatures.stream()
                .map(searchPageConverter::toFoodFeatureResponseDto)
                .toList();

        SearchPageResponseDto.FoodFeatureListResponseDto response = SearchPageResponseDto.FoodFeatureListResponseDto.builder()
                .FoodFeatureList(foodFeatureList)
                .build();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "최근 검색 기록 삭제", description = "최근 검색 기록을 삭제합니다.")
    @DeleteMapping("/recent/{recentId}")
    public ApiResponse<SearchPageResponseDto.toDeleteResultDto> deleteRecentSearch(@PathVariable Long recentId) {

        RecentSearch deleteRecentSearch = recentSearchService.getRecentSearchById(recentId);
        SearchPageResponseDto.toDeleteResultDto response = searchPageConverter.toDeleteResultDto(deleteRecentSearch);

        recentSearchService.deleteRecentSearch(recentId);
        return ApiResponse.onSuccess(response);
    }

}
