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
    private final SearchResultItemRepository searchResultItemRepository;
    private final BookmarkRepository bookmarkRepository;

    private final StoreCommandService storeCommandService;

    public StoreHomeDto getStoreHome(Long placeId, Member member) {

        Optional<Store> optionalStore = storeRepository.findByKakaoPlaceId(placeId);

        Store store;
        if (optionalStore.isEmpty()) {
            List<SearchResultItem> searchResultItems = searchResultItemRepository.findByPlaceId(placeId);
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

    public List<DaumImageResponseDto.Document> getStoreDaumImages(Long placeId) {

        Store store = storeRepository.findByKakaoPlaceId(placeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        return store.getItemDaumImages().stream()
                .map(ItemDaumImage::toDocument)
                .toList();
    }
}
