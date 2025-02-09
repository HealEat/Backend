package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.search.ItemDaumImage;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultItemRepository.SearchResultItemRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreMappingService {

    private final StoreRepository storeRepository;
    private final DaumImageService daumImageService;
    private final SearchResultItemRepository searchResultItemRepository;

    @Transactional
    public SearchResultItem docToSearchResultItem(Document document,
                                                  Set<FoodFeature> features) {


        return SearchResultItem.builder()
                .placeId(Long.parseLong(document.getId()))
                .placeName(document.getPlace_name())
                .categoryName(document.getCategory_name())
                .phone(document.getPhone())
                .addressName(document.getAddress_name())
                .roadAddressName(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .placeUrl(document.getPlace_url())
                .features(features.isEmpty() ?
                        List.of("") :
                        features.stream().map(FoodFeature::getName).toList())
                .build();
    }

    public StorePreviewDto mapToDto(Member member, SearchResultItem item) {

        Long bookmarkId = null;
        if (member != null) {
             Optional<Bookmark> optionalBookmark = member.getBookmarks().stream()
                     .filter(bookmark -> bookmark.getStore().getId().equals(item.getId()))
                     .findFirst();

             if (optionalBookmark.isPresent()) {
                 bookmarkId = optionalBookmark.get().getId();
             }
        }

        Optional<Store> optionalStore = storeRepository.findByKakaoPlaceId(item.getPlaceId());
        boolean isInDB = optionalStore.isPresent();
        Store store;
        if (isInDB) {
            store = optionalStore.get();
        } else {
            store = null;
        }
        StoreResonseDto.StoreInfoDto storeInfo = item.getStoreInfoDto();

        return StorePreviewDto.builder()
                // 가게 공통 정보
                .storeInfoDto(storeInfo)

                // 최근 리뷰 사진 한 장
                .reviewImageDto(isInDB ?
                        /*구현 필요 */ null :
                        null)

                /// Response 전용 필드
                .isInDB(isInDB)

                /// Store 필요
                .isInDBDto(isInDB ?
                        store.getIsInDBDto() :
                        null)
                // Member 필요
                .bookmarkId(bookmarkId)
                .build();
    }
}
