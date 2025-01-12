package healeat.server.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HealthPlanResponseDto {
    private Long id;
    private String duration;
    private Integer number;
    private Integer count;
    private String goal;
    private String memo;
    private List<MemoImageResponseDto> memoImages;
}
