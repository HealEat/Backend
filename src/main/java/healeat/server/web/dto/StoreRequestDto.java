package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

public class StoreRequestDto {

    @Getter
    public static class SearchFilterDto {

        Integer page = 1; // 기본값은 1페이지

        String userInput; // 검색어

        Double member_x; // 사용자 경도
        Double member_y; // 사용자 위도

        String category;
        String feature;
        Double minRating;
    }
}
