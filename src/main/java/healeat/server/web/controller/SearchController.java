package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.repository.MemberRepository;
import healeat.server.service.CategoryFeatureService;
import healeat.server.service.RecentSearchService;
import healeat.server.service.StoreCommandService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.RecentSearchResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final RecentSearchService recentSearchService;
    private final CategoryFeatureService categoryFeatureService;
    private final MemberRepository memberRepository;
    private final StoreCommandService storeCommandService;

    @Operation(summary = "요청과 검색 결과 API", description =
            """
                    Request Body에
                    1. 검색어, 사용자 x, y, 검색 기준(ACCURACY / DISTANCE) << DISTANCE의 경우 x와 y 필수
                    2. 필터 조건 : 음식 종류/특징 키워드 id 리스트, 최소 별점
                    3. 동적 정렬 기준 : NONE(기본) / TOTAL / SICK / VEGET / DIET 를 받아서 가게 목록을 조회합니다.
                    - 페이징이 적용됩니다.(페이지 당 10개)
                    - 같은 검색어, 동일한 검색 기준 및 위치(오차 범위 200m 이내)에서 캐시된 결과가 반환됩니다.
                    
                    - 홈과 다른 점은, 지도 점프 및 지도 뷰를 위해 avgX, avgY, maxDistance가 제공된다는 점입니다.""")
    @PostMapping
    public ApiResponse<StoreResponseDto.StorePreviewDtoList> getSearchResults(
            @AuthenticationPrincipal Member member,
            @CheckPage @RequestParam Integer page,
            @Valid @CheckSizeSum @RequestBody StoreRequestDto.SearchKeywordDto request) {

        Member testMember = memberRepository.findById(999L).get();

        recentSearchService.saveRecentQuery(testMember, request.getQuery());

        return ApiResponse.onSuccess(storeCommandService.searchAndMapStores(
                testMember, page, request));
    }

    @Operation(summary = "최근 검색 기록 조회", description = "최근 검색 기록을 조회합니다")
    @GetMapping("/recent")
    public ApiResponse<RecentSearchResponseDto> getAllRecentSearches(
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        Page<RecentSearch> recentSearches = recentSearchService.getRecentSearchPage(testMember.getId());

        return ApiResponse.onSuccess(SearchPageConverter.toRecentSearchResponseDto(recentSearches));
    }

    @Operation(summary = "가게 타입 검색 기록 저장 API", description = "'검색 결과 목록에서 가게에 접근'할 때에만 해당 API를" +
            " 사용하시면 됩니다.")
    @PostMapping("/{placeId}")
    public ApiResponse<RecentSearchResponseDto.SetResultDto> saveRecentStore(
            @AuthenticationPrincipal Member member, @PathVariable Long placeId) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(SearchPageConverter.toSetResultDto(
                recentSearchService.saveRecentStore(testMember, placeId)));
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


