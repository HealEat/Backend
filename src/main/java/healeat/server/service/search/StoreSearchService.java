package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.apiResponse.KakaoPlaceResponseDto;
import healeat.server.web.dto.apiResponse.KakaoPlaceResponseDto.Document;
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

    /**
     * 검색
     */
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

        return saveResultAndItemsForSearch(searchResult, kakaoResponses);
    }

    private static final double EARTH_RADIUS_METERS = 111_139; // 1도 ≈ 111.139km (약 111,139m)

    @Transactional
    protected SearchResult saveResultAndItemsForSearch(
            SearchResult searchResult,
            List<KakaoPlaceResponseDto> kakaoResponses) {

        int count = getCount(searchResult, kakaoResponses);
        if (count == 0) {
            return searchResult;
        }

        List<Document> documents = new ArrayList<>();
        kakaoResponses.forEach(response -> documents.addAll(response.getDocuments()));

        double sumX = 0, sumY = 0;
        for (Document document : documents) {
            sumX += Double.parseDouble(document.getX());
            sumY += Double.parseDouble(document.getY());

            Set<FoodFeature> features = searchFeatureService
                    .extractFeaturesForDocument(document);

            SearchResultItem item = storeMappingService
                    .docToSearchResultItem(document, features);

            searchResult.addItem(item);
        }

        double avgX = sumX / count;
        double avgY = sumY / count;

        // maxDistance 계산
        double maxDistanceDegree = 0;
        for (Document document : documents) {
            double x = Double.parseDouble(document.getX());
            double y = Double.parseDouble(document.getY());
            double distance = calculateDistance(avgX, avgY, x, y);
            maxDistanceDegree = Math.max(maxDistanceDegree, distance);
        }

        // degree -> meter 변환
        double maxMeters = maxDistanceDegree * EARTH_RADIUS_METERS;

        searchResult.setViewData(avgX, avgY, maxMeters);

        return searchResultRepository.save(searchResult);
    }

    private int getCount(SearchResult searchResult, List<KakaoPlaceResponseDto> kakaoResponses) {
        KakaoPlaceResponseDto.Meta meta = kakaoResponses.get(0).getMeta();
        KakaoPlaceResponseDto.SameName metaSameName = meta.getSame_name();

        if (metaSameName != null) {
            searchResult.setMetaData(
                    metaSameName.getKeyword(), metaSameName.getSelected_region(), metaSameName.getRegion());
        }

        return meta.getPageable_count();
    }

    // 유클리드 거리 계산
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 홈 추천
     */
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

        return saveResultAndItemsForHome(searchResult, kakaoResponses);
    }

    @Transactional
    protected SearchResult saveResultAndItemsForHome(
            SearchResult searchResult,
            List<KakaoPlaceResponseDto> kakaoResponses) {

        int count = getCount(searchResult, kakaoResponses);
        if (count == 0) {
            return searchResult;
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
