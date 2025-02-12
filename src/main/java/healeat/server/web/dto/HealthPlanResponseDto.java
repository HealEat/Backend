package healeat.server.web.dto;

import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HealthPlanResponseDto {

    List<HealthPlanOneDto> HealthPlanList;
    Integer listSize;
    Integer totalPage;
    Long totalElements;
    Boolean isFirst;
    Boolean isLast;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResultDto {

        String memberName;
        Long healthPlanId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResultDto {
        Long healthPlanId;
        LocalDateTime deletedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanOneDto{

        Long id;
        String name;
        Duration duration;
        Integer goalNumber;
        Status status;
        String goal;
        String memo;
        List<HealthPlanImageResponseDto> healthPlanImages;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanImageResponseDto {
        Long id;
        String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoResponseDto {
        Long id;
        String memo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResponseDto {
        Long id;
        Status status;
    }
}
