package healeat.server.converter;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.web.dto.HealthPlanResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // 없으면 controller 에서 bean을 찾지 못하는 오류 발생
public class HealthPlanConverter {

    public static HealthPlanResponseDto.setResultDto toSetResultDto(HealthPlan healthPlan) {

        return HealthPlanResponseDto.setResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .memberName(healthPlan.getMember().getName())
                .createdAt(healthPlan.getCreatedAt())
                .build();
    }

    public static HealthPlanResponseDto.deleteResultDto toDeleteResultDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.deleteResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .build();
    }

    public static HealthPlanResponseDto.HealthPlanListDto toHealthPlanListDto(List<HealthPlan> healthPlans) {
        List<HealthPlanResponseDto.HealthPlanOneDto> healthPlanDtoList = healthPlans.stream()
                .map(HealthPlanConverter::toHealthPlanOneDto)
                .collect(Collectors.toList());

        return  HealthPlanResponseDto.HealthPlanListDto.builder()
                    .HealthPlanList(healthPlanDtoList)
                    .build();

    }

    public static HealthPlanResponseDto.HealthPlanOneDto toHealthPlanOneDto(HealthPlan healthPlan) {
        List<HealthPlanResponseDto.HealthPlanImageResponseDto> healthPlanImages = healthPlan.getHealthPlanImages()
                .stream()
                .map(HealthPlanConverter::toHealthPlanImageResponseDto)
                .collect(Collectors.toList());

        return HealthPlanResponseDto.HealthPlanOneDto.builder()
                .id(healthPlan.getId())
                .name(healthPlan.getMember().getName()) // 로그인한 사용자의 이름 설정
                .duration(healthPlan.getDuration())
                .goalNumber(healthPlan.getGoalNumber())
                .status(healthPlan.getStatus())
                .goal(healthPlan.getGoal())
                .memo(healthPlan.getMemo())
                .healthPlanImages(healthPlanImages)
                .build();
    }

    public static List<HealthPlanResponseDto.HealthPlanImageResponseDto> toHealthPlanImageListResponseDto(
            List<HealthPlanImage> healthPlanImages) {

        return healthPlanImages.stream()
                .map(HealthPlanConverter::toHealthPlanImageResponseDto)
                .collect(Collectors.toList());
    }

    public static HealthPlanResponseDto.HealthPlanImageResponseDto toHealthPlanImageResponseDto(HealthPlanImage healthPlanImage) {
        return HealthPlanResponseDto.HealthPlanImageResponseDto.builder()
                .id(healthPlanImage.getId())
                .imageUrl(healthPlanImage.getImageUrl())
                .build();
    }

    public static HealthPlanResponseDto.MemoResponseDto toMemoResponseDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.MemoResponseDto.builder()
                .id(healthPlan.getId())
                .memo(healthPlan.getMemo())
                .build();
    }

    public static HealthPlanResponseDto.StatusResponseDto toStatusResponseDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.StatusResponseDto.builder()
                .id(healthPlan.getId())
                .status(healthPlan.getStatus())
                .build();
    }
}