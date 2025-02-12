package healeat.server.converter;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.web.dto.HealthPlanResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component // 없으면 controller 에서 bean을 찾지 못하는 오류 발생
public class HealthPlanConverter {

    public static HealthPlanResponseDto.SetResultDto toSetResultDto(HealthPlan healthPlan) {

        return HealthPlanResponseDto.SetResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .memberName(healthPlan.getMember().getName())
                .createdAt(healthPlan.getCreatedAt())
                .build();
    }

    public static HealthPlanResponseDto.DeleteResultDto toDeleteResultDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.DeleteResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public static HealthPlanResponseDto toHealthPlanResponseDto(Page<HealthPlan> healthPlans) {

        List<HealthPlanResponseDto.HealthPlanOneDto> healthPlanOneDtoList = healthPlans.stream()
                .map(HealthPlanConverter::toHealthPlanOneDto)
                .toList();

        return HealthPlanResponseDto.builder()
                .HealthPlanList(healthPlanOneDtoList)
                .listSize(healthPlanOneDtoList.size())
                .totalPage(healthPlans.getTotalPages())
                .totalElements(healthPlans.getTotalElements())
                .isFirst(healthPlans.isFirst())
                .isLast(healthPlans.isLast())
                .build();
    }

    public static HealthPlanResponseDto.HealthPlanOneDto toHealthPlanOneDto(HealthPlan healthPlan) {
        List<HealthPlanResponseDto.HealthPlanImageResponseDto> healthPlanImages = healthPlan.getHealthPlanImages()
                .stream()
                .map(HealthPlanConverter::toHealthPlanImageResponseDto)
                .toList();

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
                .toList();
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