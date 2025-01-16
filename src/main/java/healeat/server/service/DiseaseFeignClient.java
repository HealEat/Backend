package healeat.server.service;

import healeat.server.web.dto.DiseaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "disease-api-client", url = "http://apis.data.go.kr/B551182/diseaseInfoService1")
public interface DiseaseFeignClient {

    @GetMapping("/getDissNameCodeList1")
    DiseaseApiResponse getDiseases(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("numOfRows") int numOfRows,   // 한 페이지 결과 수
            @RequestParam("pageNo") int pageNo,         // 페이지 번호
            @RequestParam("sickType") int sickType,     // 상병 구분 (1: 3단 상병, 2, 4단 상병)
            @RequestParam("medTp") int medTp,           // 양방, 한방 구분 (1: 양방, 2: 한방)
            @RequestParam("diseaseType") String diseaseType,    // 질병 검색 타입 (SICK_CD: 상병코드, SICK_NM: 상병명)
            @RequestParam("searchText") String searchText       // 검색어
    );
}
