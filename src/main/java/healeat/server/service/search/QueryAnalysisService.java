package healeat.server.service.search;

import healeat.server.domain.FoodFeature;
import healeat.server.repository.FoodFeatureRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.StoreRequestDto.SearchKeywordDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto;
import healeat.server.web.dto.api_response.KakaoCoordResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAnalysisService {

    private final FoodFeatureRepository foodFeatureRepository;

    private final StoreApiClient storeApiClient;
    private final SearchInfoService searchInfoService;
    private final SearchListenerService searchListenerService;

    /**
     * 식당을 포함한 지역 검사 정보로 시작
     * 실제 API 호출에 사용할 검색 정보
     */
    @Getter @Setter
    @Builder
    public static class RealSearchInfo {

        String keyword;
        String x;
        String y;

        String selectedRegion;
        List<String> region;

        String location4ImgSearch;  // region_3depth_name 또는 landmark의 이름 (Daum API 검색용)

        // 기본값 0L (0L인 경우 무시 필요)
        Long newFeatureId;
    }

    @Transactional
    public RealSearchInfo analyze(SearchKeywordDto request) {

        String query = request.getQuery();
        String x = request.getX();
        String y = request.getY();

        // 빈 쿼리 및 음식 특징 처리
        // " ", "소화가 잘 되는 음식"
        Long newFeatId = getNewQueryAndFeatId(query);
        request.getFeatureIdList().add(newFeatId);
        if (newFeatId != 0L || query == null || query.isBlank()) {
            System.out.println("빈 쿼리 및 음식 특징 처리\n"
            + "\" \", \"소화가 잘 되는 음식\"");
            return searchInfoService.getSearchInfoByHere(x, y, newFeatId);
        }

        // 지역 인식 및 쿼리에 포함된 음식 특징 처리 로직
        return analyzeRegion(request);
    }

    /**
     * 핵심 로직
     * - 지역 인식
     * - 쿼리에 포함된 음식 특징 처리
     */
    private RealSearchInfo analyzeRegion(SearchKeywordDto request) {

        String query = request.getQuery();
        String x = request.getX();  String y = request.getY();

        // 지역 검사용 API 호출
        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                query + " 식당",
                x, y, 1, 1, "accuracy", "FD6");
        searchListenerService.incrementApiCallCount();

        KakaoPlaceResponseDto.SameName sameName = kakaoList.getMeta().getSame_name();
        String keyword = query;
        String selectedRegion = "";
        List<String> region = new ArrayList<>();

        if (sameName != null) {
            keyword = sameName.getKeyword().replaceAll("식당", "");
            selectedRegion = sameName.getSelected_region();
            region = sameName.getRegion();
        }

        // 지역명을 제외하면 음식 특징만 남는 경우 처리
        // "홍대 소화가 잘 되는 음식"
        Long newFeatId = getNewQueryAndFeatId(keyword);
        request.getFeatureIdList().add(newFeatId);
        if (newFeatId != 0L) {
            System.out.println("지역명을 제외하면 음식 특징만 남는 경우 처리\n"
            + "홍대 소화가 잘 되는 음식");
            return getCoordinatesForRegion("", newFeatId, selectedRegion, region, request);
        }

        // 지역명이 인식되는 케이스
        // "홍대", "홍대 맛집"
        if (!selectedRegion.isEmpty()) {
            System.out.println("지역명이 인식되는 케이스\n"
                    + "\"홍대\", \"홍대 맛집\"");
            return getCoordinatesForRegion(keyword, 0L, selectedRegion, region, request);

        } else {
            // 지역명이 인식되지 않는 케이스 (query == keyword)
            // "맛집" "지방간" "왕십리 소화가 잘 되는 음식"(카카오' bug)
            String noWhiteQuery = keyword.replaceAll("\\s+", "");
            List<FoodFeature> featuresInQuery = foodFeatureRepository.findByQueryContainingFeature(noWhiteQuery);

            // 마지막으로 음식 특징을 확인해야 하는 케이스
            // "지방간" "왕십리 소화가 잘 되는 음식"(target)
            if (kakaoList.getDocuments().isEmpty() && !featuresInQuery.isEmpty()) {
                System.out.println("마지막으로 음식 특징을 확인해야 하는 케이스\n"
                        + "\"지방간\" \"왕십리 소화가 잘 되는 음식\"(target)");
                return handleRegionFeatureCase(request, noWhiteQuery, featuresInQuery.get(0));
            }

            System.out.println("지역명이 인식되지 않는 케이스 (query == keyword)\n" +
                    "\"맛집\"");
            RealSearchInfo searchInfo = searchInfoService.getSearchInfoByHere(x, y, 0L);
            searchInfo.setKeyword(keyword);
            return searchInfo;
        }
    }

    private RealSearchInfo handleRegionFeatureCase(SearchKeywordDto request, String noWhiteKeyword, FoodFeature feature) {

        String x = request.getX();  String y = request.getY();
        String canBeRegionQuery = noWhiteKeyword.replaceAll(feature.getName(), "");

        // 지역 검사용 API 호출
        KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByQuery(
                canBeRegionQuery + " 식당",
                x, y, 1, 1, "accuracy", "FD6");
        searchListenerService.incrementApiCallCount();

        KakaoPlaceResponseDto.SameName sameName = kakaoList.getMeta().getSame_name();
        if (sameName == null || sameName.getSelected_region() == null) {
            // "지방간"
            return null; // 이 경우는 의미 없으므로 처리 중단: 메인 서비스에서 null 받을 시 INVALID_SEARCH_CASE 반환
        }
        // "왕십리 소화가 잘 되는 음식"
        Long newFeatureId = feature.getId();
        request.getFeatureIdList().add(newFeatureId);

        return getCoordinatesForRegion(canBeRegionQuery, newFeatureId,
                sameName.getSelected_region(), sameName.getRegion(), request);
    }

    /**
     * 특징 추출 및 id 반환
     */
    public Long getNewQueryAndFeatId(String query) {

        if (query == null || query.isBlank())
            return 0L;

        String noWhiteKeyword = query.replaceAll("\\s+", "");
        Optional<FoodFeature> foodFeatureOptional = foodFeatureRepository.findByName(noWhiteKeyword);

        if (foodFeatureOptional.isPresent()) {

            FoodFeature foodFeature = foodFeatureOptional.get();
            return foodFeature.getId();
        } else {
            return 0L;
        }
    }

    /**
     * 지역에 대한 좌표 조회
     */
    public RealSearchInfo getCoordinatesForRegion(String keyword, Long newFeatureId,
                                                  String selectedRegion, List<String> region,
                                                  SearchKeywordDto request) {

        KakaoCoordResponseDto selectedXY = storeApiClient.address2Coord(selectedRegion, 1, 1);
        searchListenerService.incrementApiCallCount();

        if (selectedXY.getDocuments().isEmpty()) {
            // 주소-좌표 변환 실패시 랜드마크로 시도
            KakaoPlaceResponseDto.Document landmark = storeApiClient.getLandmarkByQuery(
                            selectedRegion, request.getX(), request.getY(), 1, "accuracy")
                    .getDocuments().stream()
                    .findFirst().get(); // 명소를 찾는다. (카테고리 제한하지 않음) (카카오가 지역명을 인식했으므로 반드시 존재)
            searchListenerService.incrementApiCallCount();

            return RealSearchInfo.builder()
                    .keyword(keyword)
                    .x(landmark.getX())
                    .y(landmark.getY())
                    .selectedRegion(landmark.getPlace_name())
                    .region(List.of(""))
                    .location4ImgSearch(landmark.getPlace_name())
                    .newFeatureId(newFeatureId)
                    .build();
        } else {
            return searchInfoService.getSearchInfoByRegion(keyword, newFeatureId, selectedRegion, region);
        }
    }
}
