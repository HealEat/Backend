package healeat.server.converter;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.MemoImage;
import healeat.server.web.dto.HealthPlanResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class HealthPlanConverter {

    public HealthPlanResponseDto.setReusltDto toSetResultDto(HealthPlan healthPlan) {

        return HealthPlanResponseDto.setReusltDto.builder()
                .healthPlanId(healthPlan.getId())
                .memberName(healthPlan.getMember().getName())
                .createdAt(healthPlan.getCreatedAt())
                .build();
    }

/*    public HealthPlanResponseDto.HealthPlanOneDto toHealthPlanOneDto(HealthPlan healthPlan) {
        List<HealthPlanResponseDto.MemoImageResponseDto> memoImages = healthPlan.getMemoImages()
                .stream()
                .map(this::toMemoImageResponseDto)
                .collect(Collectors.toList());

        // 로그인한 사용자 이름 가져오기
        String memberName = getAuthenticatedMemberName();

        return HealthPlanResponseDto.HealthPlanOneDto.builder()
                .name(memberName) // 로그인한 사용자의 이름 설정
                .duration(healthPlan.getDuration().name())
                .goalNumber(healthPlan.getGoalNumber())
                .count(healthPlan.getCount())
                .goal(healthPlan.getGoal())
                .memo(healthPlan.getMemo())
                .memoImages(memoImages)
                .build();
    }*/

    /*public HealthPlanResponseDto.MemoImageResponseDto toMemoImageResponseDto(MemoImage memoImage) {
        return HealthPlanResponseDto.MemoImageResponseDto.builder()
                .imageUrl(memoImage.getFilePath())
                .build();
    }

    private String getAuthenticatedMemberName() {
        // Spring Security Context에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Anonymous";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getName(); // CustomUserDetails에서 이름 가져오기
        } else if (principal instanceof String) {
            return principal.toString(); // Principal이 String이면 반환
        }

        return "Unknown User";
    }*/
}