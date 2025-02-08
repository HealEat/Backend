package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.CategoryFeatureService;
import healeat.server.service.RecentSearchService;
import healeat.server.service.SearchPageService;
import healeat.server.web.dto.RecentSearchResponseDto;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MemberRepository memberRepository;

    @Operation(summary = "요청과 검색 결과 API", description =
            """
                    Request Body에
                    1. 검색어, 사용자 x, y, 검색 기준(ACCURACY / DISTANCE) << DISTANCE의 경우 x와 y 필수
                    2. 필터 조건 : 음식 종류/특징 키워드 id 리스트, 최소 별점
                    3. 동적 정렬 기준 : NONE(기본순) / TOTAL / SICK / VEGET / DIET 를 받아서 가게 목록을 조회합니다.
                    
                    위치 또는 반경의 200m 내 오차까지 동일한 캐시에서 반환됩니다.""")
    @PostMapping
    public ApiResponse<StoreResonseDto.StorePreviewDtoList> getSearchResults(
            @AuthenticationPrincipal Member member,
            @RequestParam Integer page,
            @Valid @RequestBody StoreRequestDto.SearchKeywordDto request) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(storeQueryServiceImpl.searchAndMapStores(
                testMember, page, request));
    }

    @Operation(summary = "최근 검색 기록 조회", description = "최근 검색 기록을 조회합니다")
    @GetMapping("/recent")
    public ApiResponse<RecentSearchResponseDto> getAllRecentSearches(
            @AuthenticationPrincipal Member member
    ) {

        Member testMember = memberRepository.findById(999L).get();
        return ApiResponse.onSuccess(searchPageService.getRecentSearch(testMember));
    }

    @Operation(summary = "음식 종류 조회", description = "음식 종류를 전체 조회합니다.")
    @GetMapping("/categories")
    public ApiResponse<RecentSearchResponseDto.FoodCategoryListResponseDto> getFoodCategoryLists() {

        List<FoodCategory> foodCategoryList = categoryFeatureService.getAllFoodCategories();

        return ApiResponse.onSuccess(SearchPageConverter.toFoodCategoryListResponseDto(foodCategoryList));
    }

    @Operation(summary = "음식 특징 조회", description = "음식 특징을 전체 조회합니다.")
    @GetMapping("/features")
    public ApiResponse<RecentSearchResponseDto.FoodFeatureListResponseDto> getFoodFeatureLists() {

        List<FoodFeature> foodFeatureList = categoryFeatureService.getAllFoodFeatures();

        return ApiResponse.onSuccess(SearchPageConverter.toFoodFeatureListResponseDto(foodFeatureList));
    }

    @Operation(summary = "최근 검색 기록 삭제", description = "최근 검색 기록을 삭제합니다.")
    @DeleteMapping("/recent/{recentId}")
    public ApiResponse<RecentSearchResponseDto.DeleteResultDto> deleteRecentSearch(@PathVariable Long recentId) {

        return ApiResponse.onSuccess(recentSearchService.toDeleteRecentSearch(recentId));
    }
}


