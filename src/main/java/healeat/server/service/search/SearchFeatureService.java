package healeat.server.service.search;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto.Document;
import lombok.Builder;
import lombok.Getter;
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

    // 카테고리와 특징 기반으로 필터링
    public Set<String> getFilteredPlaceIds(
            List<SearchResultItem> items,
            List<Long> categoryIdList,
            List<Long> featureIdList) {

        if ((categoryIdList == null || categoryIdList.isEmpty())
                && (featureIdList == null || featureIdList.isEmpty())) {
            return items.stream()
                    .map(SearchResultItem::getPlaceId)
                    .collect(Collectors.toSet());
        }

        Set<String> filteredIds = new HashSet<>();

        for (SearchResultItem item : items) {
            if (matchesFilters(item.getCategoryName(), categoryIdList, featureIdList)) {
                filteredIds.add(item.getPlaceId());
            }
        }

        return filteredIds;
    }

    private boolean matchesFilters(String categoryName, List<Long> categoryIdList, List<Long> featureIdList) {
        // 카테고리 필터링
        if (categoryIdList != null && !categoryIdList.isEmpty()) {
            boolean matchesCategory = categoryIdList.stream()
                    .map(foodCategoryRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(FoodCategory::getName)
                    .anyMatch(categoryName::contains);

            if (!matchesCategory) return false;
        }

        // 특징 필터링
        if (featureIdList != null && !featureIdList.isEmpty()) {
            Set<String> categoryFeatures = featureIdList.stream()
                    .map(foodFeatureRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .flatMap(feature -> featCategoryMapRepository.findAllByFoodFeature(feature).stream())
                    .map(featCategoryMap -> featCategoryMap.getFoodCategory().getName())
                    .collect(Collectors.toSet());

            return categoryFeatures.stream().anyMatch(categoryName::contains);
        }

        return true;
    }

    public List<Long> getCategoryIdList(FoodFeature foodFeature) {

        return featCategoryMapRepository.findAllByFoodFeature(foodFeature).stream()
                .map(f_c -> f_c.getFoodCategory().getId())
                .toList();
    }

    /**
     * 특징 추출 결과를 담는 record
     */
    @Getter
    public static class FeatureExtractionResult {

        String addedFeatureFromQuery;
        Set<Long> newCategoryIds;
        String processedKeyword;
        Boolean containsFeature;

        @Builder
        public FeatureExtractionResult(String addedFeatureFromQuery, Set<Long> newCategoryIds,
                                       String processedKeyword, Boolean containsFeature) {
            this.addedFeatureFromQuery = addedFeatureFromQuery;
            this.newCategoryIds = newCategoryIds;
            this.processedKeyword = processedKeyword;
            this.containsFeature = containsFeature;
        }
    }

    /**
     * 특징 추출 및 카테고리 추가
     */
    public FeatureExtractionResult extractFeatures(String keyword) {
        String noWhiteKeyword = keyword.replaceAll("\\s+", "");
        Optional<FoodFeature> foodFeatureOptional = foodFeatureRepository.findByName(noWhiteKeyword);

        Set<Long> categoryIds = new HashSet<>();
        String processedKeyword = keyword;

        FeatureExtractionResult.FeatureExtractionResultBuilder builder = FeatureExtractionResult.builder()
                .addedFeatureFromQuery("")
                .newCategoryIds(categoryIds)
                .processedKeyword(processedKeyword)
                .containsFeature(false);

        // 쿼리에 지역명을 제외하고 음식 특징이 존재
        //  -> 필터에 추가, 카카오 API 쿼리에서는 제거
        if (foodFeatureOptional.isPresent()) {
            categoryIds.addAll(getCategoryIdList(foodFeatureOptional.get()));
            processedKeyword = "";

            builder.addedFeatureFromQuery(noWhiteKeyword)
                    .newCategoryIds(categoryIds)
                    .processedKeyword(processedKeyword)
                    .containsFeature(true);
        }

        return builder.build();
    }

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
