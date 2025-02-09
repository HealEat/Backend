package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
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
    public static class HealthPlanMemoUpdateRequestDto {
        private String memo;
    }

    @Getter
    public static class HealthPlanStatusUpdateRequestDto {
        private Status status;
    }
}