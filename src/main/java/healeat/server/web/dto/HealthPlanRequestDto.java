package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import lombok.*;

import java.util.List;

public class HealthPlanRequestDto {

    @Getter
    public static class HealthPlanUpdateRequestDto {
        private Duration duration;
        private Integer number;
        private String goal;
    }

    @Getter
    public class HealthPlanImageUpdateRequestDto {
        private List<Integer> indicesToUpdate; // 수정할 이미지 인덱스 리스트
        private List<String> newImageExtensions; // 새 이미지 확장자 리스트
    }
}