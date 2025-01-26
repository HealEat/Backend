package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.StoreRepository;
import healeat.server.service.search.QueryAnalysisService.RealSearchInfo;
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

    public SearchResultItem storeToSearchResultItem(Store store) {

        return SearchResultItem.builder()
                .isInDB(true)
                .placeId(store.getId().toString())
                .placeName(store.getPlaceName())
                .categoryName(store.getCategoryName())
                .phone(store.getPhone())
                .addressName(store.getAddressName())
                .roadAddressName(store.getRoadAddressName())
                .x(store.getX())
                .y(store.getY())
                .placeUrl(store.getPlaceUrl())
                .reviewCount(store.getReviewCount())
                .totalScore(store.getTotalScore())
                .sickScore(store.getSickScore())
                .vegetScore(store.getVegetScore())
                .dietScore(store.getDietScore())
                .build();
    }

    public SearchResultItem mapDocumentToSearchResultItem(RealSearchInfo realSearchInfo,
                                                          Document document,
                                                          Set<FoodFeature> features) {

        // DB에 존재하는 가게일 때
        Optional<Store> optionalStore = storeRepository.findById(Long.parseLong(document.getId()));
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            SearchResultItem searchResultItem = storeToSearchResultItem(store);

            List<String> fileNameList = store.getReviews().stream()
                    .map(review -> {
                        Optional<ReviewImage> firstImage = review.getReviewImageList().stream().findFirst();
                        return firstImage.map(ReviewImage::getFileName).orElse("");
                    }).toList();

            searchResultItem.addReviewImgsToHead(fileNameList);     // review 이미지들 앞에 추가

            searchResultItem.setChangableData(realSearchInfo.getLocation4ImgSearch(),     // headForAPI
                                            Integer.valueOf(document.getDistance()),    // distance
                                            features.isEmpty() ?                        // features
                                            List.of("") :
                                            features.stream().map(FoodFeature::getName).toList());
            return searchResultItem;
        }

        return SearchResultItem.builder()
                .isInDB(false)
                .placeId(document.getId())
                .placeName(document.getPlace_name())
                .categoryName(document.getCategory_name())
                .phone(document.getPhone())
                .addressName(document.getAddress_name())
                .roadAddressName(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .placeUrl(document.getPlace_url())
                .reviewCount(0)
                .totalScore(0.0f)
                .sickScore(0.0f)
                .vegetScore(0.0f)
                .dietScore(0.0f)

                .imageUrlList(List.of(""))      // Daum 이미지 API는 프론트엔드에서 호출

                .headForAPI(realSearchInfo.getLocation4ImgSearch())
                .distance(Integer.valueOf(document.getDistance()))
                .features(features.isEmpty() ?
                        List.of("") :
                        features.stream().map(FoodFeature::getName).toList())
                .build();
    }

    public StorePreviewDto mapToDto(Member member, SearchResultItem item) {
        boolean isBookMarked = false;
        if (member != null) {
            isBookMarked = member.getBookmarks().stream()
                    .anyMatch(bookmark -> item.getId().equals(bookmark.getStore().getId()));
        }

        return StorePreviewDto.builder()
                .isInDB(item.getIsInDB())
                .placeId(Long.valueOf(item.getPlaceId()))
                .headForAPI(item.getHeadForAPI())
                .placeName(item.getPlaceName())
                .categoryName(item.getCategoryName())
                .phone(item.getPhone())
                .addressName(item.getAddressName())
                .roadAddressName(item.getRoadAddressName())
                .x(item.getX())
                .y(item.getY())
                .placeUrl(item.getPlaceUrl())
                .distance(String.valueOf(item.getDistance()))
                .imageUrlList(item.getImageUrlList())
                .features(item.getFeatures())
                .reviewCount(item.getReviewCount())
                .totalScore(item.getTotalScore())
                .sickScore(item.getSickScore())
                .vegetScore(item.getVegetScore())
                .dietScore(item.getDietScore())

                .isBookMarked(isBookMarked)
                .build();
    }
}
