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
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.search.ItemDaumImage;
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
import healeat.server.web.dto.api_response.DaumImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static healeat.server.web.dto.StoreResonseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class StoreQueryServiceImpl {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final SearchResultItemRepository searchResultItemRepository;
    private final BookmarkRepository bookmarkRepository;

    private final StoreCommandService storeCommandService;

    public StoreHomeDto getStoreHome(Long storeId, Member member) {

        Optional<Store> optionalStore = storeRepository.findByKakaoPlaceId(storeId);

        Store store;
        if (optionalStore.isEmpty()) {
            List<SearchResultItem> searchResultItems = searchResultItemRepository.findByPlaceId(storeId);
            if (searchResultItems.isEmpty()) {
                throw new StoreHandler(ErrorStatus.STORE_NOT_FOUND);
            }
            store = storeCommandService.saveStore(searchResultItems.get(0));
        } else {
            store = optionalStore.get();
        }

        Optional<Bookmark> optionalBookmark = bookmarkRepository.findByMemberAndStore(member, store);
        // NPE 발생 가능성 없는 코드!
        Long bookmarkId = optionalBookmark.map(Bookmark::getId).orElse(null);

        StoreHomeDto storeHomeDto = store.getStoreHomeDto();
        storeHomeDto.setBookmarkId(bookmarkId);
        storeHomeDto.setIsInDBDto(store.getIsInDBDto());

        return storeHomeDto;
    }

    public List<DaumImageResponseDto.Document> getStoreDaumImages(Long storeId) {

        Store store = storeRepository.findByKakaoPlaceId(storeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        return store.getItemDaumImages().stream()
                .map(ItemDaumImage::toDocument)
                .toList();
    }

    public Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder) {

        Store store = storeRepository.findByKakaoPlaceId(storeId).orElseThrow(() ->
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
