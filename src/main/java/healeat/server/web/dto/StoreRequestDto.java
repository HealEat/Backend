package healeat.server.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class StoreRequestDto {

    @Getter
    public static class HealEatRequestDto {

        String x; // 추천받을 위치 경도
        String y; // 추천받을 위치 위도

        Integer radius; // 조사할 반경
    }

    @Getter
    @Builder
    public static class SearchKeywordDto {

        String query; // 검색어

        String x; // 사용자 경도
        String y; // 사용자 위도

        List<Long> categoryIdList;

        List<Long> featureIdList;
    }
}
