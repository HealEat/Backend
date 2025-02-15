package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

public class HealthPlanRequestDto {

    @Getter
    public static class HealthPlanUpdateRequestDto {

        @Pattern(regexp = "^(DAY|WEEK|DAY10|MONTH)$", message = "기간이 올바르지 않습니다")
        String duration;
        Integer number;
        String goal;

        List<Long> removeImageIds;
    }

    @Getter
    public static class HealthPlanStatusUpdateRequestDto {

        @Pattern(regexp = "^(FAIL|PROGRESS|COMPLETE)$", message = "상태가 올바르지 않습니다")
        String status;
    }
}