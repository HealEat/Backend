package healeat.server.service.search;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFeatureService {

    private final FoodFeatureRepository foodFeatureRepository;
    private final FeatCategoryMapRepository featCategoryMapRepository;
    private final FoodCategoryRepository foodCategoryRepository;

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
    public Set<String> getFilteredPlaceIds(
            List<SearchResultItem> items,
            Set<Long> categoryIdList,
            Set<Long> featureIdList) {

        if ((categoryIdList == null || categoryIdList.isEmpty())
                && (featureIdList == null || featureIdList.isEmpty())) {
            return items.stream()
                    .map(SearchResultItem::getPlaceId)
                    .collect(Collectors.toSet());
        }

        Set<String> filteredPlaceIds = new HashSet<>();

        for (SearchResultItem item : items) {
            if (matchesFilters(item.getCategoryName(), categoryIdList, featureIdList)) {
                filteredPlaceIds.add(item.getPlaceId());
            }
        }

        return filteredPlaceIds;
    }

    private boolean matchesFilters(String categoryName, Set<Long> categoryIdList, Set<Long> featureIdList) {
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
}
