package healeat.server.service.search;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.SearchHandler;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.service.search.QueryAnalysisService.RealSearchInfo;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto.Document;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreSearchService {

    private final SearchResultRepository searchResultRepository;

    private final StoreApiClient storeApiClient;
    private final QueryAnalysisService queryAnalysisService;
    private final SearchFeatureService searchFeatureService;
    private final StoreMappingService storeMappingService;

    private final SearchListenerService searchListenerService;

    @Transactional
    public SearchResult searchAndSave(
            StoreRequestDto.SearchKeywordDto request) {

        // 초기 검색 정보가 동일하면 캐시 반환
        SearchResultKey initKey = SearchResultKey.builder()
                .query(request.getQuery())
                .x(request.getX())
                .y(request.getY())
                .build();
        Optional<SearchResult> optionalInitResult = searchResultRepository.findByInitIdAndCreatedAtAfter(
                initKey.generateId(), LocalDateTime.now().minusMinutes(30));
        if (optionalInitResult.isPresent()) {

            System.out.println("초기 검색 정보를 통해 캐시가 반환됩니다.");
            return optionalInitResult.get();
        }

        // 핵심 로직 호출
        // 카카오 로컬 API 활용
        RealSearchInfo realSearchInfo = queryAnalysisService.analyze(request);
        if (realSearchInfo == null) {
            throw new SearchHandler(ErrorStatus.INVALID_SEARCH_CASE);
        }

        SearchResultKey searchKey = SearchResultKey.builder()
                .query(realSearchInfo.getKeyword())
                .x(realSearchInfo.getX())
                .y(realSearchInfo.getY())
                .build();

        // 캐시에서 먼저 찾음
        Optional<SearchResult> optionalSearchResult = searchResultRepository.findBySearchIdAndCreatedAtAfter(
                searchKey.generateId(), LocalDateTime.now().minusMinutes(30));

        if (optionalSearchResult.isPresent()) {     // 없다면 캐시에 저장
            System.out.println("로직을 통해 알아낸 정보로 캐시가 반환됩니다.");
            return optionalSearchResult.get();
        }

        System.out.println("캐시가 없으므로 새로 저장합니다.");
        List<Document> documents = getDocsOnLoopByQuery(searchKey);

        SearchResult result = createSearchResult(realSearchInfo, searchKey, initKey, documents);

        System.out.println("SearchResult: searchId=" + result.getSearchId() +
                ", query=" + result.getQuery() +
                ", items=" + result.getItems().size());
        return result;
    }

    @Transactional
    protected SearchResult createSearchResult(
            RealSearchInfo realSearchInfo,
            SearchResultKey searchKey,
            SearchResultKey initKey,
            List<Document> documents) {

        SearchResult result = SearchResult.builder()
                .searchId(searchKey.generateId())
                .initId(initKey.generateId())
                .baseX(searchKey.x())
                .baseY(searchKey.y())
                .query(searchKey.query())
                .selectedRegion(realSearchInfo.getSelectedRegion())
                .otherRegions(realSearchInfo.getRegion())
                .build();

        documents.forEach(document -> {
            Set<FoodFeature> features =
                    searchFeatureService.extractFeaturesForDocument(document);
            SearchResultItem item =
                    storeMappingService.mapDocumentToSearchResultItem(
                            realSearchInfo, document, features);
            result.addItem(item);
        });

        return searchResultRepository.save(result);
    }

    private List<Document> getDocsOnLoopByQuery(SearchResultKey searchResultKey) {
        String query = searchResultKey.query();
        String x = searchResultKey.x();     String y = searchResultKey.y();
        if (query == null || query.isBlank()) {
            return getDocsOnLoopByLocation(x, y);
        }

        List<Document> documents = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {
            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                    query, x, y, pageIter++, 15, "accuracy", "FD6");
            searchListenerService.incrementApiCallCount();

            documents.addAll(kakaoList.getDocuments());
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return documents;
    }

    private List<KakaoPlaceResponseDto.Document> getDocsOnLoopByLocation(String x, String y) {
        List<KakaoPlaceResponseDto.Document> documents = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByLocation(
                    x, y, pageIter++, "accuracy", "FD6");
            searchListenerService.incrementApiCallCount();

            documents.addAll(kakaoList.getDocuments());
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return documents;
    }

}
