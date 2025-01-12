package healeat.server.converter;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.web.dto.HealthPlanResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // 없으면 controller 에서 bean을 찾지 못하는 오류 발생
public class HealthPlanConverter {

    public HealthPlanResponseDto.setResultDto toSetResultDto(HealthPlan healthPlan) {

        return HealthPlanResponseDto.setResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .memberName(healthPlan.getMember().getName())
                .createdAt(healthPlan.getCreatedAt())
                .build();
    }

    public HealthPlanResponseDto.deleteResultDto toDeleteResultDto(HealthPlan healthPlan) {
        return HealthPlanResponseDto.deleteResultDto.builder()
                .healthPlanId(healthPlan.getId())
                .build();
    }

    public HealthPlanResponseDto.HealthPlanOneDto toHealthPlanOneDto(HealthPlan healthPlan) {
        List<HealthPlanResponseDto.MemoImageResponseDto> memoImages = healthPlan.getHealthPlanImages()
                .stream()
                .map(this::toMemoImageResponseDto)
                .collect(Collectors.toList());

        return HealthPlanResponseDto.HealthPlanOneDto.builder()
                .name(healthPlan.getMember().getName()) // 로그인한 사용자의 이름 설정
                .duration(healthPlan.getDuration())
                .goalNumber(healthPlan.getGoalNumber())
                .count(healthPlan.getCount())
                .goal(healthPlan.getGoal())
                .memo(healthPlan.getMemo())
                .memoImages(memoImages)
                .build();
    }

    public HealthPlanResponseDto.MemoImageResponseDto toMemoImageResponseDto(HealthPlanImage healthPlanImage) {
        return HealthPlanResponseDto.MemoImageResponseDto.builder()
                .id(healthPlanImage.getId())
                .imageUrl(healthPlanImage.getFilePath())
                .build();
    }
}