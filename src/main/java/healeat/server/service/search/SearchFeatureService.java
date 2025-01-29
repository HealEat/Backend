package healeat.server.service.search;

import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.FeatCategoryMap;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.FeatCategoryMapRepository.FeatCategoryMapRepository;
import healeat.server.repository.FoodCategoryRepository;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFeatureService {

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

    public List<Long> getFilteredPlaceIds(List<SearchResultItem> searchResultItems,
                                          Set<Long> categoryIds,
                                          Set<Long> featureIds) {

        // feature -> category id Set으로 모음
        Set<Long> idsForFilter = new HashSet<>(categoryIds);
        idsForFilter.addAll(
                featCategoryMapRepository.findCategoryIdsByFeatureIds(featureIds));

        // 성능을 위해, 한 번에 모든 카테고리를 조회하여 Map 생성
        Map<Long, String> categoryMap = foodCategoryRepository
                .findAllById(idsForFilter).stream()
                .collect(Collectors.toMap(FoodCategory::getId, FoodCategory::getName));
        // Map을 통해 id를 이름으로 매핑
        List<String> categoryNamesForFilter = featureIds.stream()
                .map(categoryMap::get)
                .toList();

        return searchResultItems.stream()
                .filter(item ->
                        categoryNamesForFilter.stream()
                                .anyMatch(iterName ->
                                        item.getCategoryName().contains(iterName))
                )
                .map(SearchResultItem::getId)
                .toList();
    }
}