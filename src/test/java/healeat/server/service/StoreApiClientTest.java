package healeat.server.service;

import healeat.server.web.dto.DaumImageResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
//public class StoreApiClientTest {
//
//    @Autowired
//    private StoreApiClient storeApiClient;
//
//    @Test
//    public void 쿼리_없이_가게_검색() throws Exception {
//
//        KakaoPlaceResponseDto response = storeApiClient.getStoresSimply("126.951399056142", "37.5580680839066",
//                1, "distance");
//
//        if (response != null && response.getDocuments() != null && response.getMeta() != null) {
//            System.out.println(response);
//        } else {
//            System.out.println("응답 또는 일부 데이터가 null입니다.");
//        }
//    }
//
//    @Test
//    public void 쿼리로_가게_검색() throws Exception {
//
//        KakaoPlaceResponseDto response = storeApiClient.getStoresByQuery("왕십리 맛집", "126.951399056142", "37.5580680839066",
//                1, 15, "accuracy");
//
//        if (response != null && response.getDocuments() != null && response.getMeta() != null) {
//            System.out.println(response);
//        } else {
//            System.out.println("응답 또는 일부 데이터가 null입니다.");
//        }
//    }
//
//    @Test
//    public void 가게_이미지_검색() throws Exception {
//        DaumImageResponseDto response = storeApiClient.getImagesByQuery("수성동4가 허대구대구통닭 본점", 1, 10);
//
//        if (response != null && response.getDocuments() != null && response.getMeta() != null) {
//            System.out.println(response);
//        } else {
//            System.out.println("응답 또는 일부 데이터가 null입니다.");
//        }
//    }
//}
