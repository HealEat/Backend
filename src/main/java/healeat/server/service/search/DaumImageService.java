package healeat.server.service.search;

import healeat.server.service.StoreApiClient;
import healeat.server.web.dto.apiResponse.DaumImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DaumImageService {

    private final StoreApiClient storeApiClient;

    @Cacheable(value = "daumImgDocuments", key = "T(java.lang.String).format('%s_%s', " +
            "#placeName, #addressName)")
    public List<DaumImageResponseDto.Document> getDaumImagesWithNameInfo(String placeName, String addressName) {

        boolean nameContainsRegion = placeName.contains(" ") && placeName.endsWith("점");

        DaumImageResponseDto daumResponse;

        if (nameContainsRegion) {
            daumResponse = storeApiClient.getDaumByQuery(placeName, 1, 15);
        } else {
            String[] addressParts = addressName.split(" ");
            String region3DepthName = addressParts.length >= 3 ? addressParts[2] : "";
            boolean isEmpty3Depth = region3DepthName.isEmpty();
            daumResponse = storeApiClient.getDaumByQuery(isEmpty3Depth ?
                            placeName :
                            region3DepthName + " " + placeName
                    , 1, 15);
        }

        return daumResponse.getDocuments();
    }
}
