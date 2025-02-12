package healeat.server.service.search;

import healeat.server.domain.*;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.ReviewImageRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.repository.StoreThumbnailRepository;
import healeat.server.service.ReviewService;
import healeat.server.web.dto.apiResponse.DaumImageResponseDto;
import healeat.server.web.dto.apiResponse.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResponseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreMappingService {

    private final StoreRepository storeRepository;
    private final DaumImageService daumImageService;
    private final ReviewService reviewService;
    private final ReviewImageRepository reviewImageRepository;
    private final StoreThumbnailRepository storeThumbnailRepository;

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
        Long bookmarkId = (member == null) ? null :
                member.getBookmarks().stream()
                        .filter(bookmark -> bookmark.getPlaceId().equals(item.getPlaceId()))
                        .map(Bookmark::getId)
                        .findFirst()
                        .orElse(null);

        Long placeId = item.getPlaceId();

        // Store 존재 여부 확인 및 객체 가져오기
        Store store = storeRepository.findByKakaoPlaceId(placeId).orElse(null);
        boolean isInDB = store != null;

        // 가게 썸네일 관련
        String imageUrl = storeThumbnailRepository.findByPlaceId(placeId)
                .map(StoreThumbnail::getImageUrl)
                .orElseGet(() -> {
                    if (isInDB) {
                        return reviewImageRepository.findFirstByReview_StoreOrderByCreatedAtDesc(store)
                                .map(ReviewImage::getImageUrl)
                                .orElseGet(() -> getDaumImage(item));
                    }
                    return getDaumImage(item);
                });

        return StorePreviewDto.builder()

                .storeInfoDto(item.getStoreInfoDto())

                .imageUrl(imageUrl)

                .isInDBDto(isInDB ? store.getIsInDBDto() : null)

                .bookmarkId(bookmarkId)
                .build();
    }

    private String getDaumImage(SearchResultItem item) {
        return daumImageService.getDaumImagesWithNameInfo(item.getPlaceName(), item.getAddressName())
                .stream()
                .findFirst()
                .map(DaumImageResponseDto.Document::getImage_url)
                .orElse("");
    }
}
