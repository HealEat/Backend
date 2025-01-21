package healeat.server.service;

import healeat.server.config.FeignConfig;
import healeat.server.web.dto.DaumImageResponseDto;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.KakaoXYResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "storeApiClient",
        url = "https://dapi.kakao.com", configuration = FeignConfig.class)
public interface StoreApiClient {

    /**
     * 카카오 맵 API
     */
    @GetMapping("/v2/local/search/category.json")
    KakaoPlaceResponseDto getKakaoByLocation(   /* 쿼리가 없다 */
                                             @RequestParam(defaultValue = "") String x,
                                             @RequestParam(defaultValue = "") String y,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "distance") String sort,
                                             @RequestParam(defaultValue = "FD6") String category_group_code);

    @GetMapping("/v2/local/search/keyword.json")
    KakaoPlaceResponseDto getKakaoByQuery(  // 일반 검색(필터링)에 사용
                                            @RequestParam String query, // 쿼리 존재
                                            @RequestParam(defaultValue = "") String x,
                                            @RequestParam(defaultValue = "") String y,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "accuracy") String sort,
                                            @RequestParam(defaultValue = "FD6") String category_group_code);

    @GetMapping("/v2/local/search/keyword.json")
    KakaoPlaceResponseDto getHotPlaceByQuery(  // 일반 검색(필터링)에 사용
                                            @RequestParam String query, // 쿼리 존재
                                            @RequestParam(defaultValue = "") String x,
                                            @RequestParam(defaultValue = "") String y,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "accuracy") String sort);

    @GetMapping("/v2/local/search/address.json")
    KakaoXYResponseDto addressToXY( // 주소를 좌표로 변환 - 검색 시 지역명이 포함될 때 사용
                                    @RequestParam String query,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "1") Integer size);

    /**
     * Daum 이미지 검색 API
     *  띄워쓰기와 ~점으로 끝나지 않는 가게에 대해
     *  식당 이름 앞에
     *  "OO동(리)(...) "(마지막 띄워쓰기 앞. -알고리즘)을 붙임.
     */
    @GetMapping("/v2/search/image")
    DaumImageResponseDto getDaumByQuery(@RequestParam String query,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size);
}
