package healeat.server.service.search;

import healeat.server.service.StoreApiClient;
import healeat.server.service.search.QueryAnalysisService.RealSearchInfo;
import healeat.server.web.dto.api_response.KakaoAddressResponse;
import healeat.server.web.dto.api_response.KakaoCoordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchInfoService {

    private final StoreApiClient storeApiClient;

    private final SearchListenerService searchListenerService;

    public RealSearchInfo getSearchInfoByHere(String x, String y) {

        if ((x != null && !x.isEmpty()) && (y != null && !y.isEmpty())) {
            KakaoAddressResponse apiResponse = storeApiClient.coord2Address(x, y);
            searchListenerService.incrementApiCallCount();

            KakaoAddressResponse.Address address = apiResponse.getDocuments().get(0).getAddress();
            String addressName = address.getAddress_name();

            return RealSearchInfo.builder()
                    .keyword("")
                    .x(x)
                    .y(y)
                    .selectedRegion(addressName)
                    .region(List.of(""))
                    .build();
        } else {
            return RealSearchInfo.builder()
                    .keyword("")
                    .x("")
                    .y("")
                    .selectedRegion("")
                    .region(List.of(""))
                    .build();
        }
    }

    public RealSearchInfo getSearchInfoByRegion(String keyword, String selectedRegion, List<String> region) {

        KakaoCoordResponseDto apiResponse = storeApiClient.address2Coord(selectedRegion, 1, 1);
        searchListenerService.incrementApiCallCount();

        KakaoCoordResponseDto.Document document = apiResponse.getDocuments().get(0);

        return RealSearchInfo.builder()
                .keyword(keyword)
                .x(document.getX())
                .y(document.getY())
                .selectedRegion(selectedRegion)
                .region(region)
                .build();
    }
}
