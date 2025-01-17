package healeat.server.service;

import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

//@SpringBootTest
//@Transactional(readOnly = true)
//public class SearchTest {
//
//    @Autowired private StoreQueryService storeQueryService;
//
//    @Test
//    public void API_호출테스트_및_호출횟수확인() {
//
//        StoreRequestDto.SearchFilterDto request = StoreRequestDto.SearchFilterDto.builder()
//                .x("126.951399056142")
//                .y("37.5580680839066")
//                .query("명동역 소화가 잘되는 음식")
//                .page(1)
//                .categoryIdList(Arrays.asList(/*1L, 30L*/)) // 한식 > 육류,고기 || 일식
//                .featureIdList(Arrays.asList(/*7L, 13L, 15L*//*10L*/)) // 담백 || 짜지않은 || 야채
//                .minRating(0.0f)
//                .build();
//
//        System.out.println(storeQueryService.searchByFilter(request));
//        System.out.println(storeQueryService.getApiCallCount());
//    }
//}
