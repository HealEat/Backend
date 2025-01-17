package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.SearchPageConverter;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.service.CategoryFeatureService;
import healeat.server.service.RecentSearchService;
import healeat.server.service.SearchPageService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.SearchPageResponseDto;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;
    private final SearchPageService searchPageService;
    private final RecentSearchService recentSearchService;
    private final CategoryFeatureService categoryFeatureService;

    @Operation(summary = "검색 화면", description = "쿼리 스트링으로 검색어와 필터 조건" +
            "(사용자 위치, 음식 종류/특징 키워드, 최소 별점)을 받아서 가게 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<StoreResonseDto.StorePreviewDtoList> getSearchResults(
            @CheckPage @RequestParam Integer page,
            @CheckSizeSum @ModelAttribute StoreRequestDto.SearchKeywordDto request,
            @RequestParam(defaultValue = "0") Float minRating) {

        return ApiResponse.onSuccess(StoreConverter.toStorePreviewListDto(
                storeQueryServiceImpl.mapDocumentWithDB(page, request, minRating)));
    }

    @Operation(summary = "검색창 구현", description = "검색창을 조회합니다.(음식 종류, 음식 특징, 최근 검색 목록 포함된 페이지)")
    @GetMapping("/recent")
    public ApiResponse<SearchPageResponseDto> getAllRecentSearches() {

        return ApiResponse.onSuccess(searchPageService.getAllSearchPage());
    }

    @Operation(summary = "음식 종류 조회", description = "음식 종류를 전체 조회합니다.")
    @GetMapping("/categories")
    public ApiResponse<SearchPageResponseDto.FoodCategoryListResponseDto> getFoodCategoryLists() {

        List<FoodCategory> foodCategoryList = categoryFeatureService.getAllFoodCategories();

        return ApiResponse.onSuccess(SearchPageConverter.toFoodCategoryListResponseDto(foodCategoryList));
    }

    @Operation(summary = "음식 특징 조회", description = "음식 특징을 전체 조회합니다.")
    @GetMapping("/features")
    public ApiResponse<SearchPageResponseDto.FoodFeatureListResponseDto> getFoodFeatureLists() {

        List<FoodFeature> foodFeatureList = categoryFeatureService.getAllFoodFeatures();

        return ApiResponse.onSuccess(SearchPageConverter.toFoodFeatureListResponseDto(foodFeatureList));
    }

    @Operation(summary = "최근 검색 기록 삭제", description = "최근 검색 기록을 삭제합니다.")
    @DeleteMapping("/recent/{recentId}")
    public ApiResponse<SearchPageResponseDto.toDeleteResultDto> deleteRecentSearch(@PathVariable Long recentId) {

        return ApiResponse.onSuccess(recentSearchService.toDeleteRecentSearch(recentId));
    }
}


