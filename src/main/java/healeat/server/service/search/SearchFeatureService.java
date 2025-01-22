package healeat.server.service.search;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.FoodCategoryHandler;
import healeat.server.apiPayload.exception.handler.FoodFeatureHandler;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.repository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchFeatureService {

    private final FoodFeatureRepository foodFeatureRepository;
    private final FeatCategoryMapRepository featCategoryMapRepository;
    private final FoodCategoryRepository foodCategoryRepository;


    public Set<Long> getCategoryIdListByKeywordIds(List<Long> featureIdList, List<Long> categoryIdList) {

        // 계산용 카테고리 ID Set
        Set<Long> categoryIdSet = new HashSet<>();

        if (categoryIdList != null && !categoryIdList.isEmpty()) { // 선택한 음식 종류 필터가 존재할 때
            categoryIdSet.addAll(categoryIdList.stream()
                    .map(foodCategoryRepository::findById)
                    .map(op_c -> op_c.orElseThrow(
                            () -> new FoodCategoryHandler(ErrorStatus.FOOD_CATEGORY_NOT_FOUND)
                    ).getId())
                    .toList());
        }

        if (featureIdList != null && !featureIdList.isEmpty()) { // 선택한 음식 특징 필터가 존재할 때

            featureIdList.stream()
                    .map(id -> getCategoryIdList(
                                    foodFeatureRepository.findById(id).orElseThrow(() ->
                                            new FoodFeatureHandler(ErrorStatus.FOOD_FEATURE_NOT_FOUND))
                            )
                    )
                    .forEach(categoryIdSet::addAll);
        }

        return categoryIdSet;
    }

    public List<Long> getCategoryIdList(FoodFeature foodFeature) {

        return featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                .map(f_c -> f_c.getFoodCategory().getId())
                .toList();
    }

    /**
     * 특징 추출 결과를 담는 record
     */
    @Builder
    public record FeatureExtractionResult(Set<Long> updatedCategoryIds,
                                          String processedKeyword,
                                          boolean containsFeature) {
    }

    /**
     * 특징 추출 및 카테고리 추가 (캐시 대상)
     */
    public FeatureExtractionResult extractFeatures(String keyword, Set<Long> categoryIdSet) {
        String noWhiteKeyword = keyword.replaceAll("\\s+", "");
        Optional<FoodFeature> foodFeatureOptional = foodFeatureRepository.findByName(noWhiteKeyword);

        Set<Long> updatedCategoryIds = new HashSet<>(categoryIdSet);
        String processedKeyword = keyword;
        boolean containsFeature = false;

        // 쿼리에 지역명을 제외하고 음식 특징이 존재
        //  -> 필터에 추가, 카카오 API 쿼리에서는 제거
        if (foodFeatureOptional.isPresent()) {
            updatedCategoryIds.addAll(getCategoryIdList(foodFeatureOptional.get()));
            processedKeyword = "";
            containsFeature = true;
        }

        return FeatureExtractionResult.builder()
                .updatedCategoryIds(updatedCategoryIds)
                .processedKeyword(processedKeyword)
                .containsFeature(containsFeature)
                .build();
    }

    // 캐시 적용 가능
    public Set<FoodFeature> extractFeaturesForDocument(Document document) {
        return foodCategoryRepository.findAll().stream()
                .filter(fc -> document.getCategory_name().contains(fc.getName()))
                .flatMap(fc -> {
                    List<FeatCategoryMap> featCategoryMaps = featCategoryMapRepository.findByFoodCategory(fc);
                    return featCategoryMaps.stream()
                            .map(FeatCategoryMap::getFoodFeature);
                })
                .collect(Collectors.toSet());
    }
}
