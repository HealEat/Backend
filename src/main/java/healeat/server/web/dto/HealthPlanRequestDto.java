package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
import lombok.*;

import java.util.List;

public class HealthPlanRequestDto {

    @Getter
    public static class HealthPlanUpdateRequestDto {
        Duration duration;
        Integer number;
        String goal;
    }

    @Getter
    public static class HealthPlanMemoUpdateRequestDto {
        String memo;
    }

    @Getter
    public static class HealthPlanStatusUpdateRequestDto {
        Status status;
    }
}