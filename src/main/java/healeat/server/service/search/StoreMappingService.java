package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.StoreRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.api_response.DaumImageResponseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreMappingService {

    private final StoreRepository storeRepository;
    private final DaumImageService daumImageService;

    public SearchResultItem docToSearchResultItem(Document document,
                                                  Set<FoodFeature> features) {

        String distance = document.getDistance();

        return SearchResultItem.builder()
                .distance(Integer.valueOf(distance.isEmpty() ?
                        "0" :
                        distance))
                .features(features.isEmpty() ?
                        List.of("") :
                        features.stream().map(FoodFeature::getName).toList())
                // Daum 이미지 API
                // SearchResultItem은 정렬을 위해 존재하는 엔티티이므로: 이미지는 저장 X
                .daumImageUrlList(List.of(""))

                .placeId(Long.parseLong(document.getId()))
                .placeName(document.getPlace_name())
                .categoryName(document.getCategory_name())
                .phone(document.getPhone())
                .addressName(document.getAddress_name())
                .roadAddressName(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .placeUrl(document.getPlace_url())
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

        Optional<Store> optionalStore = storeRepository.findById(item.getId());
        boolean isInDB = optionalStore.isPresent();
        StoreResonseDto.IsInDBDto isInDBDto = null;

        StoreResonseDto.StoreImageListDto storeImageListDto;
        if (isInDB) {
            Store store = optionalStore.get();
            isInDBDto = store.getIsInDBDto();

            storeImageListDto = StoreResonseDto.StoreImageListDto.builder()
                    .reviewImagePreviewDtoList(store.getReviewImagePreviewDtoList())
                    .daumImgDocuments(store.getDaumImgDocuments())
                    .build();

        } else {

            storeImageListDto = StoreResonseDto.StoreImageListDto.builder()
                    .reviewImagePreviewDtoList(List.of())
                    // Daum 이미지 API 호출
                    .daumImgDocuments(
                        daumImageService.getDaumImgDocuments(item))
                    .build();
        }

        StoreResonseDto.StoreInfoDto storeInfo = item.getStoreInfoDto();

        return StorePreviewDto.builder()
                // 가게 공통 정보
                .storeInfoDto(storeInfo)

                // Store_reviewImages + API 합친 정보
                .storeImageListDto(storeImageListDto)

                /// Response 전용 필드
                .isInDB(isInDB)

                /// Store 필요
                .isInDBDto(isInDBDto) // null일 수 있음.
                // Member 필요
                .bookmarkId(bookmarkId)
                .build();
    }
}
