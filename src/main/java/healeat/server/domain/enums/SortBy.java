package healeat.server.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SortBy {

    DEFAULT,
    SICK,
    VEGET,
    DIET;

    @JsonCreator
    public static SortBy from(String value) {
        try {
            return SortBy.valueOf(value);
        } catch (Exception e) {
            return DEFAULT;  // 잘못된 값이 들어왔을 때 기본값 반환
        }
    }
}
