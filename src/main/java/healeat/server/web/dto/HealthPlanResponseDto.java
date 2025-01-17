package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HealthPlanResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class setResultDto {

        private String memberName;
        private Long healthPlanId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class deleteResultDto {
        private Long healthPlanId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanListDto{
       private List<HealthPlanOneDto> HealthPlanList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanOneDto{

        private String name;
        private Duration duration;
        private Integer goalNumber;
        private Integer count;
        private String goal;
        private String memo;
        private List<MemoImageResponseDto> memoImages;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoImageResponseDto {
        private Long id;
        private String imageUrl;
    }

}
