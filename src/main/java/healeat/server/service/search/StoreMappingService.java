package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto.StorePreviewDtoBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StoreMappingService {
    private final StoreRepository storeRepository;
    private final SearchFeatureService searchFeatureService;

    // 캐시 적용 가능
    public StorePreviewDto mapDocumentToStorePreview(Document document) {
        Set<FoodFeature> featureSet = searchFeatureService.extractFeaturesForDocument(document);
        List<String> features = convertFeatureSetToList(featureSet);

        StorePreviewDtoBuilder builder = createBasicStorePreviewBuilder(document, features);
        enrichBuilderWithStoreData(builder, document.getId());

        return builder.build();
    }

    private List<String> convertFeatureSetToList(Set<FoodFeature> featureSet) {
        return featureSet.isEmpty() ? List.of("") :
                featureSet.stream().map(FoodFeature::getName).toList();
    }

    private StorePreviewDtoBuilder createBasicStorePreviewBuilder(
            Document document, List<String> features) {

        return StorePreviewDto.builder()
                .id(Long.parseLong(document.getId()))
                .place_name(document.getPlace_name())
                .category_name(document.getCategory_name())
                .phone(document.getPlace_url())
                .address_name(document.getAddress_name())
                .road_address_name(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .place_url(document.getPlace_url())
                .distance(document.getDistance())
                .features(features)
                .reviewCount(0)
                .totalScore(0.0f)
                .sickScore(0.0f)
                .vegetScore(0.0f)
                .dietScore(0.0f)
                .isBookMarked(false);
    }

    // 캐시 적용 가능
    private void enrichBuilderWithStoreData(StorePreviewDtoBuilder builder, String storeId) {
        storeRepository.findById(Long.parseLong(storeId))
                .ifPresent(store -> {
                    builder.reviewCount(store.getReviewCount())
                            .totalScore(store.getTotalScore())
                            .sickScore(store.getSickScore())
                            .vegetScore(store.getVegetScore())
                            .dietScore(store.getDietScore())
                            .isBookMarked(false);
                });
    }
}
