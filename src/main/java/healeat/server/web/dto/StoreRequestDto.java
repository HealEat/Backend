package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

public class StoreRequestDto {

    @Getter
    public static class SearchFilterDto {

        Integer page = 1; // 기본값은 1페이지

        String userInput; // 검색어

        String x; // 사용자 경도
        String y; // 사용자 위도
        Integer radius; // 검색할 반경 -지도 화면 기준
        String rect; // 검색 결과를 제한할 화면

        String category;
        String feature;
        Double minRating;
    }
}
