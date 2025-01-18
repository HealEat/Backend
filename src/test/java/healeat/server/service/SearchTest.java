package healeat.server.service;

import healeat.server.web.dto.StoreRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

//@SpringBootTest
//@Transactional(readOnly = true)
//public class SearchTest {
//
//    @Autowired private StoreQueryServiceImpl storeQueryServiceImpl;
//
//    @Test
//    public void API_호출테스트_및_호출횟수확인() {
//
//        StoreRequestDto.SearchKeywordDto request = StoreRequestDto.SearchKeywordDto.builder()
//                .x("127.10274196113546")
//                .y("37.56684449678786")
//                .query("아차산 식당") // 쿼리에 식당을 추가
//                .categoryIdList(Arrays.asList(/*1L, 30L*/)) // 한식 > 육류,고기 || 일식
//                .featureIdList(Arrays.asList(/*7L, 13L, 15L*/9L)) // 담백 || 짜지않은 || 야채
//                .build();
//
//        System.out.println(storeQueryServiceImpl.getDocumentsByKeywords(request));
//        System.out.println(storeQueryServiceImpl.getApiCallCount());
//    }
//}
