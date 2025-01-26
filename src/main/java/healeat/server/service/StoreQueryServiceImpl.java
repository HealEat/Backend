package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
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
import healeat.server.service.search.SearchListenerService;
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
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static healeat.server.web.dto.StoreResonseDto.*;
import static healeat.server.web.dto.StoreResonseDto.StorePreviewDto;

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

    private final SearchListenerService searchListenerService;

    @Transactional
    public void saveStore(Long kakaoStoreId) {


    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     * -> 이후 컨트롤러에 위임
     */
    @Transactional
    public Pair<Page<StorePreviewDto>, SearchInfo> searchAndMapStores(
            @AuthenticationPrincipal Member member,
            @CheckPage Integer page,
            @CheckSizeSum StoreRequestDto.SearchKeywordDto request) {

        // 1. 검색 및 결과 저장
        SearchResult searchResult = storeSearchService.searchAndSave(request);
        System.out.println("searchResult items size: " + searchResult.getItems().size());
        System.out.println("searchResult: " + searchResult);

        int apiCallCount = searchListenerService.getAndResetApiCallCount();
        long newFeatureId = searchListenerService.getAndResetFeatureId();

        // 2. category와 feature 기반으로 필터링할 placeId 목록 조회
        Set<String> filteredPlaceIds = searchFeatureService.getFilteredPlaceIds(
                searchResult.getItems(),
                request.getCategoryIdList(),
                request.getFeatureIdList()
        );
        System.out.println("Filtered Place IDs: " + filteredPlaceIds);

        if (filteredPlaceIds.isEmpty()) {

            return Pair.of(Page.empty(), searchResult.toSearchInfo(newFeatureId, apiCallCount));

        } else {
            // 3. 페이지 요청 생성 (페이지는 0부터 시작하므로 page - 1)
            Pageable pageable = PageRequest.of(page - 1, 10);

            // 4. 정렬된 검색 결과 조회
            Page<SearchResultItem> items = searchResultItemRepository.findSortedStores(
                    searchResult,
                    filteredPlaceIds,
                    request.getMinRating(),
                    request.getSortBy(),
                    pageable
            );

            System.out.println("Page content size: " + items.getContent().size());
            System.out.println("Page content: " + items.getContent());

            // 5. DTO 변환 및 결과 반환
            return Pair.of(
                    items.map(item -> {
                        StorePreviewDto dto = storeMappingService.mapToDto(member, item);
                        System.out.println("Mapped DTO: " + dto);
                        return dto;
                    }),
                    searchResult.toSearchInfo(newFeatureId, apiCallCount)
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
