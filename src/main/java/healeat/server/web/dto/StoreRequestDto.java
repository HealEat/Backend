package healeat.server.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public class StoreRequestDto {

    @Getter
    public static class HealEatRequestDto {

        String x; // 추천받을 위치 경도
        String y; // 추천받을 위치 위도

        Integer radius; // 조사할 반경
    }

    @Getter
    public static class SearchKeywordDto {

        String query; // 검색어

        String x; // 사용자 경도
        String y; // 사용자 위도

        Set<Long> categoryIdList;

        Set<Long> featureIdList;

        @Max(value = 5, message = "최소 별점은 5 이하여야 합니다")
        @Min(value = 0, message = "최소 별점은 0 이상이어야 합니다")
        Float minRating;

        @Pattern(regexp = "^(ACCURACY|DISTANCE)$", message = "검색 기준이 올바르지 않습니다")
        String searchBy;

        @Pattern(regexp = "^(NONE|TOTAL|SICK|VEGET|DIET)$", message = "정렬 기준이 올바르지 않습니다")
        String sortBy;
    }
}
