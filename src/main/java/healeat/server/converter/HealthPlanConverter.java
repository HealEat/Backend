package healeat.server.converter;

import healeat.server.domain.HealthPlan;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.MemoImageResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class HealthPlanConverter {

    public static HealthPlanResponseDto toDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.builder()
                .id(healthPlan.getId())
                .duration(healthPlan.getDuration().name())
                .number(healthPlan.getNumber())
                .count(healthPlan.getCount())
                .goal(healthPlan.getGoal())
                .memo(healthPlan.getMemo())
                .memoImages(
                        healthPlan.getMemoImages() != null
                                ? healthPlan.getMemoImages().stream()
                                .map(memoImage -> MemoImageResponseDto.builder()
                                        .id(memoImage.getId())
                                        .imageUrl(memoImage.getImageUrl())
                                        .build())
                                .collect(Collectors.toList())
                                : List.of()
                )
                .build();
    }
}
