package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class HealthPlanResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResultDto {

        private String memberName;
        private Long healthPlanId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultDto {
        private Long healthPlanId;
        private LocalDateTime deletedAt;
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

        private Long id;
        private String name;
        private Duration duration;
        private Integer goalNumber;
        private Status status;
        private String goal;
        private String memo;
        private List<HealthPlanImageResponseDto> healthPlanImages;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanImageResponseDto {
        private Long id;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoResponseDto {
        private Long id;
        private String memo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResponseDto {
        private Long id;
        private Status status;
    }
}
