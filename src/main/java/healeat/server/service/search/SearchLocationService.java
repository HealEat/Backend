package healeat.server.service.search;

import healeat.server.repository.FoodCategoryRepository;
import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoXYResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static healeat.server.service.search.StoreSearchService.getDocuments;

@Service
@RequiredArgsConstructor
public class SearchLocationService {

    private final StoreApiClient storeApiClient;
    private final FoodCategoryRepository foodCategoryRepository;

    public List<KakaoPlaceResponseDto.Document> getDocsOnLoopByLocation(String x, String y, Set<Long> categoryIdSet, Integer fromPage) {
        List<KakaoPlaceResponseDto.Document> filteredList = new ArrayList<>();
        int pageIter = fromPage;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByLocation(
                    x, y, pageIter++, "accuracy", "FD6");

            filteredList.addAll(filterAndGetDocuments(kakaoList, categoryIdSet));
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return filteredList;
    }

    /**
     * 지역에 대한 좌표 조회 (캐시 대상)
     */
    public Pair<String, String> getCoordinatesForRegion(String address, String x, String y) {
        KakaoXYResponseDto selectedXY = storeApiClient.addressToXY(address, 1, 1);

        if (selectedXY.getDocuments().isEmpty()) {
            // 주소-좌표 변환 실패시 랜드마크로 시도
            KakaoPlaceResponseDto.Document landmark = storeApiClient.getLandmarkByQuery(
                            address, x, y, 1, "accuracy")
                    .getDocuments().stream()
                    .findFirst().get(); // 명소를 찾는다. (음식점 한정 X)
            return Pair.of(landmark.getX(), landmark.getY());
        } else {
            KakaoXYResponseDto.Document location = selectedXY.getDocuments().stream().findFirst().get();
            return Pair.of(location.getX(), location.getY());
        }
    }

    private List<KakaoPlaceResponseDto.Document> filterAndGetDocuments(KakaoPlaceResponseDto kakaoList, Set<Long> categoryIdSet) {

        return getDocuments(kakaoList, categoryIdSet, foodCategoryRepository);
    }
}
