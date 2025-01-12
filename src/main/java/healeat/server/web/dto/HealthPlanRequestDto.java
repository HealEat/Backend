package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import lombok.*;

public class HealthPlanRequestDto {

    @Getter
    public static class HealthPlanUpdateRequestDto {
        private Duration duration;
        private Integer number;
        private String goal;
    }
}