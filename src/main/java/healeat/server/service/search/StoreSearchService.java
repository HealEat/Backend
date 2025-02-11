package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreSearchService {

    private final SearchResultRepository searchResultRepository;

    private final StoreApiClient storeApiClient;
    private final SearchFeatureService searchFeatureService;
    private final StoreMappingService storeMappingService;

    private final ApiCallCountService apiCallCountService;

    @Transactional
    @Cacheable(value = "searchResult", key = "T(java.lang.String).format('%s_%s_%s_%s', " +
            "(#request.query != null and #request.query != '' ? " +
                "#request.query : 'default_query'), " +
            "(#request.x != null and #request.x != '' ? " +               // 약 200m 오차 허용 (/ 111320)
                "T(java.lang.Math).round(T(java.lang.Double).parseDouble(#request.x) / 0.0018) : 0), " +
            "(#request.y != null and #request.y != '' ? " +               // 약 200m 오차 허용 (/ 111320)
                "T(java.lang.Math).round(T(java.lang.Double).parseDouble(#request.y) / 0.0018) : 0), " +
            "(#request.searchBy eq 'ACCURACY'))")
    public SearchResult searchAndSave(
            StoreRequestDto.SearchKeywordDto request) {

        SearchResult searchResult = SearchResult.builder()
                .query(request.getQuery())
                .baseX(request.getX())
                .baseY(request.getY())
                .accuracy(request.getSearchBy().equals("ACCURACY"))
                .build();

        searchResultRepository.save(searchResult);

        List<KakaoPlaceResponseDto> kakaoResponses = get3ResponsesByQuery(searchResult);

        return saveSearchResultAndItems(searchResult, kakaoResponses);
    }

    @Transactional
    @Cacheable(value = "recommendResult", key = "T(java.lang.String).format('%s_%s_%s', " +
            "(#request.x != null and #request.x != '' ? " +               // 약 200m 오차 허용 (/ 111320)
                "T(java.lang.Math).round(T(java.lang.Double).parseDouble(#request.x) / 0.0018) : 0), " +
            "(#request.y != null and #request.y != '' ? " +               // 약 200m 오차 허용 (/ 111320)
                "T(java.lang.Math).round(T(java.lang.Double).parseDouble(#request.y) / 0.0018) : 0), " +
            "(#request.radius != null ? " +            // 약 200m 오차 허용
                "T(java.lang.Math).round(#request.radius / 200.0) : 0))")
    public SearchResult recommendAndSave(
            StoreRequestDto.HealEatRequestDto request) {

        String x = request.getX();
        String y = request.getY();
        int radius = request.getRadius();
        SearchResult searchResult = SearchResult.builder()
                .query("for-home-recommend")
                .baseX(x)
                .baseY(y)
                .radius(radius)
                .accuracy(false)
                .build();

        searchResultRepository.save(searchResult);

        List<KakaoPlaceResponseDto> kakaoResponses = get3ResponsesForHome(x, y, radius);

        return saveSearchResultAndItems(searchResult, kakaoResponses);
    }

    @Transactional
    protected SearchResult saveSearchResultAndItems(
            SearchResult searchResult,
            List<KakaoPlaceResponseDto> kakaoResponses) {

        KakaoPlaceResponseDto.SameName metaData = kakaoResponses.get(0).getMeta().getSame_name();
        if (metaData != null) {
            searchResult.setMetaData(
                    metaData.getKeyword(), metaData.getSelected_region(), metaData.getRegion());
        }

        List<Document> documents = new ArrayList<>();
        kakaoResponses.forEach(response -> documents.addAll(response.getDocuments()));

        documents.forEach(document -> {
            Set<FoodFeature> features = searchFeatureService
                    .extractFeaturesForDocument(document);

            SearchResultItem item = storeMappingService
                    .docToSearchResultItem(document, features);

            searchResult.addItem(item);
        });

        return searchResultRepository.save(searchResult);
    }

    private List<KakaoPlaceResponseDto> get3ResponsesByQuery(SearchResult searchResult) {

        String query = searchResult.getQuery();
        String x = searchResult.getBaseX();
        String y = searchResult.getBaseY();
        String sort = searchResult.getAccuracy() ? "accuracy" : "distance";

        if (query == null || query.isBlank()) {
            return get3ResponsesByLocation(x, y, sort);
        }

        List<KakaoPlaceResponseDto> responses = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoResponse = storeApiClient.getKakaoByQuery(
                    query, x, y, pageIter++, sort, "FD6");
            apiCallCountService.incrementApiCallCount();

            responses.add(kakaoResponse);
            if (kakaoResponse.getMeta().getIs_end())
                break;
        }
        return responses;
    }

    private List<KakaoPlaceResponseDto> get3ResponsesByLocation(String x, String y, String sort) {
        List<KakaoPlaceResponseDto> responses = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoResponse = storeApiClient.getKakaoByLocation(
                    x, y, pageIter++, sort, "FD6");
            apiCallCountService.incrementApiCallCount();

            responses.add(kakaoResponse);
            if (kakaoResponse.getMeta().getIs_end())
                break;
        }
        return responses;
    }

    private List<KakaoPlaceResponseDto> get3ResponsesForHome(String x, String y, Integer radius) {
        List<KakaoPlaceResponseDto> responses = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoResponse = storeApiClient.getKakaoForHome(
                    x, y, radius, pageIter++, "distance", "FD6");
            apiCallCountService.incrementApiCallCount();

            responses.add(kakaoResponse);
            if (kakaoResponse.getMeta().getIs_end())
                break;
        }
        return responses;
    }
}
