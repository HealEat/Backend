package healeat.server.service.search;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.FeatCategoryMapRepository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFeatureService {

    private final FeatCategoryMapRepository featCategoryMapRepository;
    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodFeatureRepository foodFeatureRepository;

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

    // 카테고리와 특징 기반으로 필터링
    public List<Long> getFilteredItemIds(
            List<SearchResultItem> items,
            Set<Long> categoryIdList,
            Set<Long> featureIdList) {

        if ((categoryIdList == null || categoryIdList.isEmpty())
                && (featureIdList == null || featureIdList.isEmpty())) {
            return items.stream()
                    .map(SearchResultItem::getId)
                    .toList();
        }

        assert categoryIdList != null;
        Map<Long, String> categoryIdToNameMap = foodCategoryRepository.findAllById(categoryIdList)
                .stream()
                .collect(Collectors.toMap(FoodCategory::getId, FoodCategory::getName));

        Map<Long, Set<String>> featureIdToCategoryNameMap = foodFeatureRepository.findAllById(featureIdList)
                .stream()
                .collect(Collectors.toMap(
                        FoodFeature::getId,
                        feature -> featCategoryMapRepository.findAllByFoodFeature(feature).stream()
                                .map(featCategoryMap -> featCategoryMap.getFoodCategory().getName())
                                .collect(Collectors.toSet())
                ));

        return items.stream()
                .filter(item -> matchesFilters(
                        item.getCategoryName(),
                        categoryIdList,
                        categoryIdToNameMap,
                        featureIdToCategoryNameMap)
                )
                .map(SearchResultItem::getId)
                .toList();
    }

    private boolean matchesFilters(String categoryName,
                                   Set<Long> categoryIdList,
                                   Map<Long, String> categoryIdToNameMap,
                                   Map<Long, Set<String>> featureIdToCategoryNamesMap) {
        // 카테고리 필터링
        if (categoryIdList != null && !categoryIdList.isEmpty()) {
            boolean matchesCategory = categoryIdList.stream()
                    .map(categoryIdToNameMap::get)
                    .anyMatch(categoryName::contains);

            if (!matchesCategory) return false;
        }

        if (featureIdToCategoryNamesMap != null && !featureIdToCategoryNamesMap.isEmpty()) {
            Set<String> categoryFeatures = featureIdToCategoryNamesMap.values()
                    .stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            return categoryFeatures.stream().anyMatch(categoryName::contains);
        }

        return true;
    }
}