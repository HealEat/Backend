package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.*;
import healeat.server.service.search.StoreMappingService;
import healeat.server.service.search.StoreSearchService;
import healeat.server.service.search.StoreSortingService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static healeat.server.web.dto.StoreResonseDto.*;
import static healeat.server.web.dto.StoreResonseDto.StorePreviewDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class StoreQueryServiceImpl {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    private final StoreSearchService storeSearchService;
    private final StoreMappingService storeMappingService;
    private final StoreSortingService storeSortingService;

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     * -> 이후 컨트롤러에 위임
     */
    public Pair<Page<StorePreviewDto>, SearchInfo> searchAndMapStores(
            @CheckPage Integer page,
            @CheckSizeSum StoreRequestDto.SearchKeywordDto request,
            Float minRating) {

        // 1. 검색
        Pair<List<Document>, SearchInfo> searchResult =
                storeSearchService.searchStores(request);

        // 2. 매핑
        List<StorePreviewDto> mappedStores = searchResult.getFirst().stream()
                .map(storeMappingService::mapDocumentToStorePreview)
                .toList();

        // 3. 정렬
        List<StorePreviewDto> sortedStores =
                storeSortingService.sortStoresByPreference(mappedStores, minRating);

        // 4. 페이징
        Page<StorePreviewDto> pagedResult =
                CustomPagination.toPage(sortedStores, page - 1, 10);

        return Pair.of(pagedResult, searchResult.getSecond());
    }

    public Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        if (sort == null) sort = SortBy.LATEST;

        // 페이지 번호를 0-based로 조정
        int adjustedPage = Math.max(0, page - 1);

        Sort.Direction direction = getSortDirection(sortOrder);

        Sort sorting;
        PageRequest pageable;

        switch (sort) {
            case SICK:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_DiseasesNotEmpty(store, pageable);
            case VEGET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Vegetarian(store, Vegetarian.NONE, pageable);
            case DIET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Diet(store, Diet.NONE, pageable);
            case LATEST:
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
