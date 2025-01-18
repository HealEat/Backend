package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Store;
import healeat.server.service.CategoryFeatureService;
import healeat.server.service.RecentSearchService;
import healeat.server.service.SearchPageService;
import healeat.server.service.StoreQueryService;
import healeat.server.web.dto.SearchPageResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final StoreQueryService storeQueryService;
    private final SearchPageService searchPageService;
    private final RecentSearchService recentSearchService;
    private final CategoryFeatureService categoryFeatureService;

    @Operation(summary = "검색 화면", description = "쿼리 스트링으로 검색어와 필터 조건" +
            "(사용자 위치, 음식 종류/특징 키워드, 최소 별점)을 받아서 가게 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<StoreResonseDto.StorePreviewListDto> getSearchResults(
            @ModelAttribute StoreRequestDto.SearchFilterDto request) {

        Page<Store> searchPage = storeQueryService.searchByFilter(request);

        return ApiResponse.onSuccess(StoreConverter.toStorePreviewListDto(searchPage));
    }

    @Operation(summary = "검색창 구현", description = "검색창을 조회합니다.(음식 종류, 음식 특징, 최근 검색 목록 포함된 페이지)")
    @GetMapping("/searchPage")
    public ApiResponse<SearchPageResponseDto.AllSearchPageResponseDto> getAllRecentSearches() {

        SearchPageResponseDto.AllSearchPageResponseDto response = searchPageService.getAllSearchPage();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "음식 종류 조회", description = "음식 종류를 전체 조회합니다.")
    @GetMapping("/foodCategory")
    public ApiResponse<SearchPageResponseDto.FoodCategoryListResponseDto> getFoodCategoryLists(){

        SearchPageResponseDto.FoodCategoryListResponseDto response = categoryFeatureService.getAllFoodCategoryPage();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "음식 특징 조회", description = "음식 특징을 전체 조회합니다.")
    @GetMapping("/foodFeature")
    public ApiResponse<SearchPageResponseDto.FoodFeatureListResponseDto> getFoodFeatureLists(){

        SearchPageResponseDto.FoodFeatureListResponseDto response = categoryFeatureService.getAllFoodFeaturePage();

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "최근 검색 기록 삭제", description = "최근 검색 기록을 삭제합니다.")
    @DeleteMapping("/recent/{recentId}")
    public ApiResponse<SearchPageResponseDto.toDeleteResultDto> deleteRecentSearch(@PathVariable Long recentId) {

        SearchPageResponseDto.toDeleteResultDto response = recentSearchService.toDeleteRecentSearch(recentId);

        return ApiResponse.onSuccess(response);
    }

}


