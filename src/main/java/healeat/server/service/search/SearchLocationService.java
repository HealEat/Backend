package healeat.server.service.search;

import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoXYResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchLocationService {

    private final StoreApiClient storeApiClient;
    private final StoreApiCallService storeApiCallService;

    public List<KakaoPlaceResponseDto.Document> getDocsOnLoopByLocation(String x, String y) {
        List<KakaoPlaceResponseDto.Document> documents = new ArrayList<>();
        int pageIter = 1;
        while (pageIter <= 3) {

            KakaoPlaceResponseDto kakaoList = storeApiClient.getKakaoByLocation(
                    x, y, pageIter++, "accuracy", "FD6");
            storeApiCallService.incrementApiCallCount();

            documents.addAll(kakaoList.getDocuments());
            if (kakaoList.getMeta().getIs_end())
                break;
        }
        return documents;
    }

    /**
     * 지역에 대한 좌표 조회
     */
    public Pair<String, String> getCoordinatesForRegion(String address, String x, String y) {
        KakaoXYResponseDto selectedXY = storeApiClient.addressToXY(address, 1, 1);
        storeApiCallService.incrementApiCallCount();

        if (selectedXY.getDocuments().isEmpty()) {
            // 주소-좌표 변환 실패시 랜드마크로 시도
            KakaoPlaceResponseDto.Document landmark = storeApiClient.getLandmarkByQuery(
                            address, x, y, 1, "accuracy")
                    .getDocuments().stream()
                    .findFirst().get(); // 명소를 찾는다. (카테고리 제한하지 않음) (카카오가 지역명을 인식했으므로 반드시 존재)
            storeApiCallService.incrementApiCallCount();

            return Pair.of(landmark.getX(), landmark.getY());
        } else {
            KakaoXYResponseDto.Document location = selectedXY.getDocuments().stream().findFirst().get();
            return Pair.of(location.getX(), location.getY());
        }
    }
}
