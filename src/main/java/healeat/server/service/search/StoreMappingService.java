package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.ReviewImageRepository;
import healeat.server.repository.SearchResultItemRepository.SearchResultItemRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.service.ReviewService;
import healeat.server.web.dto.ReviewResponseDto;
import healeat.server.web.dto.StoreResponseDto;
import healeat.server.web.dto.api_response.DaumImageResponseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResponseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
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
    private final ReviewService reviewService;
    private final ReviewImageRepository reviewImageRepository;

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
        ReviewResponseDto.ReviewImageDto reviewImageDto = null;
        DaumImageResponseDto.Document daumDocument = null;
        Store store;
        if (isInDB) {
            store = optionalStore.get();
            // 리뷰 이미지 최신 하나
            Optional<ReviewImage> optionalReviewImage =
                    reviewImageRepository.findFirstByReview_StoreOrderByCreatedAtDesc(store);

            if (optionalReviewImage.isPresent()) {

                ReviewImage reviewImage = optionalReviewImage.get();

                reviewImageDto = ReviewResponseDto.ReviewImageDto.builder()
                        .reviewId(reviewImage.getId())
                        .imageUrl(reviewImage.getImageUrl())
                        .reviewerInfo(reviewImage.getReview().getReviewerInfo())
                        .build();
            } else {
                // 다음 이미지 제일 앞의 하나
                List<DaumImageResponseDto.Document> documents =
                        daumImageService.getDaumImagesWithNameInfo(item.getPlaceName(), item.getAddressName());
                daumDocument = documents.isEmpty() ? null : documents.get(0);
            }

        } else {
            store = null;
            // 다음 이미지 제일 앞의 하나
            List<DaumImageResponseDto.Document> documents =
                    daumImageService.getDaumImagesWithNameInfo(item.getPlaceName(), item.getAddressName());
            daumDocument = documents.isEmpty() ? null : documents.get(0);
        }
        StoreResponseDto.StoreInfoDto storeInfo = item.getStoreInfoDto();

        return StorePreviewDto.builder()
                // 가게 공통 정보
                .storeInfoDto(storeInfo)

                // 최근 리뷰 사진 한 장
                .reviewImageDto(reviewImageDto)
                // Daum 사진 한 장
                .daumDocument(daumDocument)

                /// Store 필요
                .isInDBDto(isInDB ?
                        store.getIsInDBDto() :
                        null)
                // Member 필요
                .bookmarkId(bookmarkId)
                .build();
    }
}
