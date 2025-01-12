package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HealthPlanUpdateRequestDto {
    private Duration duration;
    private Integer number;
    private String goal;
}