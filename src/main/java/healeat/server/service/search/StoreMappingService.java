package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StoreMappingService {
    private final StoreRepository storeRepository;

    public SearchResultItem mapDocumentToSearchResultItem(Document document, Set<FoodFeature> features) {

        SearchResultItem.SearchResultItemBuilder builder = SearchResultItem.builder()
                .placeId(document.getId())
                .placeName(document.getPlace_name())
                .categoryName(document.getCategory_name())
                .phone(document.getPhone())
                .addressName(document.getAddress_name())
                .roadAddressName(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .placeUrl(document.getPlace_url())
                .distance(document.getDistance())
                .features(features.isEmpty() ?
                        List.of("") :
                        features.stream().map(FoodFeature::getName).toList())
                .reviewCount(0)
                .totalScore(0.0f)
                .sickScore(0.0f)
                .vegetScore(0.0f)
                .dietScore(0.0f);

        storeRepository.findById(Long.parseLong(document.getId()))
                .ifPresent(store -> builder
                        .reviewCount(store.getReviewCount())
                        .totalScore(store.getTotalScore())
                        .sickScore(store.getSickScore())
                        .vegetScore(store.getVegetScore())
                        .dietScore(store.getDietScore()));

        return builder.build();
    }

    public StorePreviewDto mapToDto(SearchResultItem item) {
        return StorePreviewDto.builder()
                .id(Long.parseLong(item.getPlaceId()))
                .place_name(item.getPlaceName())
                .category_name(item.getCategoryName())
                .phone(item.getPhone())
                .address_name(item.getAddressName())
                .road_address_name(item.getRoadAddressName())
                .x(item.getX())
                .y(item.getY())
                .place_url(item.getPlaceUrl())
                .distance(item.getDistance())
                .features(item.getFeatures())
                .reviewCount(item.getReviewCount())
                .totalScore(item.getTotalScore())
                .sickScore(item.getSickScore())
                .vegetScore(item.getVegetScore())
                .dietScore(item.getDietScore())
                .isBookMarked(false)
                .build();
    }
}
