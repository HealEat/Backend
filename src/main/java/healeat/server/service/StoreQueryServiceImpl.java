package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.*;
import healeat.server.repository.SearchResultItemRepository.SearchResultItemRepository;
import healeat.server.service.search.ApiCallCountService;
import healeat.server.service.search.SearchFeatureService;
import healeat.server.service.search.StoreMappingService;
import healeat.server.service.search.StoreSearchService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static healeat.server.web.dto.StoreResonseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class StoreQueryServiceImpl {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final SearchResultItemRepository searchResultItemRepository;

    private final StoreSearchService storeSearchService;
    private final SearchFeatureService searchFeatureService;
    private final StoreMappingService storeMappingService;

    private final ApiCallCountService apiCallCountService;

    @Transactional
    public Store saveStore(StoreRequestDto.ForSaveStoreDto request) {

        Store store = Store.builder()
                .id(Long.parseLong(request.getPlaceId()))
                .placeName(request.getPlaceName())
                .categoryName(request.getCategoryName())
                .phone(request.getPhone())
                .addressName(request.getAddressName())
                .roadAddressName(request.getRoadAddressName())
                .x(request.getX())
                .y(request.getY())
                .placeUrl(request.getPlaceUrl())
                .daumImgUrlList(request.getDaumImgUrlList()) // Daum 이미지 API
                .build();

        return storeRepository.save(store);
    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     */
    @Transactional
    public StorePreviewDtoList searchAndMapStores(
            Member member,
            @CheckPage Integer page,
            @CheckSizeSum StoreRequestDto.SearchKeywordDto request) {

        // 1. 검색 및 결과 저장
        SearchResult searchResult = storeSearchService.searchAndSave(request);

        int apiCallCount = apiCallCountService.getAndResetApiCallCount(); // API 호출 횟수 기록

        SearchInfo searchInfo = StoreConverter
                .toSearchInfo(member, searchResult, apiCallCount);

        // 2. category와 feature로 필터링된 placeId 리스트
        List<Long> filteredItemIds = searchFeatureService.getFilteredItemIds(
                searchResult.getItems(),
                request.getCategoryIdList(),
                request.getFeatureIdList()
        );

        if (filteredItemIds.isEmpty()) {
            return StoreConverter.toStorePreviewListDto(
                    Page.empty(),
                    searchInfo
            );

        } else {
            // 3. 페이지 요청 생성
            int safePage = Math.max(0, page - 1);
            Pageable pageable = PageRequest.of(safePage, 10);

            // 4. 정렬된 검색 결과 조회
            Page<SearchResultItem> items = searchResultItemRepository.findSortedStores(
                    searchResult,
                    filteredItemIds,
                    request.getSortBy(),
                    request.getMinRating(),
                    pageable
            );

            // 5. DTO 변환 및 결과 반환
            return StoreConverter.toStorePreviewListDto(
                    items.map(item -> storeMappingService.mapToDto(member, item)),
                    searchInfo
            );
        }
    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     */
    @Transactional
    public StorePreviewDtoList recommendAndMapStores(
            Member member,
            @CheckPage Integer page,
            StoreRequestDto.HealEatRequestDto request) {

        // 1. 검색 및 결과 저장
        SearchResult searchResult = storeSearchService.recommendAndSave(request);

        int apiCallCount = apiCallCountService.getAndResetApiCallCount(); // API 호출 횟수 기록

        SearchInfo searchInfo = StoreConverter
                .toSearchInfo(member, searchResult, apiCallCount);

        // 2. 멤버의 healEatFoods(카테고리 이름 리스트) 로 필터링된 placeId 리스트
        List<Long> filteredItemIds = searchFeatureService.getHealEatItemIds(
                searchResult.getItems(),
                member.getHealEatFoods()
        );

        if (filteredItemIds.isEmpty()) {
            return StoreConverter.toStorePreviewListDto(
                    Page.empty(),
                    searchInfo
            );

        } else {
            // 3. 페이지 요청 생성
            int safePage = Math.max(0, page - 1);
            Pageable pageable = PageRequest.of(safePage, 10);

            // 4. 정렬된 검색 결과 조회
            Page<SearchResultItem> items = searchResultItemRepository.findSortedStores(
                    searchResult,
                    filteredItemIds,
                    "NONE",
                    0.0f,
                    pageable
            );

            // 5. DTO 변환 및 결과 반환
            return StoreConverter.toStorePreviewListDto(
                    items.map(item -> storeMappingService.mapToDto(member, item)),
                    searchInfo
            );
        }
    }

    public Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        if (sort == null) sort = SortBy.DEFAULT;

        // 페이지 번호를 0-based로 조정
        int adjustedPage = Math.max(0, page - 1);

        Sort.Direction direction = getSortDirection(sortOrder);

        Sort sorting;
        PageRequest pageable;

        switch (sort) {
            case SICK:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_MemberDiseasesNotEmpty(store, pageable);
            case VEGET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Vegetarian(store, Vegetarian.NONE, pageable);
            case DIET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Diet(store, Diet.NONE, pageable);
            case DEFAULT: // 기본은 최신 순
                sorting = Sort.by(direction, "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStore(store, pageable);
            default:
                throw new SortHandler(ErrorStatus.SORT_NOT_FOUND);
        }
    }

    private Sort.Direction getSortDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
