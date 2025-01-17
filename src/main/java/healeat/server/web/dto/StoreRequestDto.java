package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

public class StoreRequestDto {

    @Getter
    public static class SearchFilterDto {

        Integer page = 1; // 기본값은 1페이지

        String query; // 검색어

        String x; // 사용자 경도
        String y; // 사용자 위도

        List<Long> categoryIdList;

        List<Long> featureIdList;

        Double minRating;
    }
}
