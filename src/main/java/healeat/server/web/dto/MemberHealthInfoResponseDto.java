package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberHealthInfoResponseDto {
    private Long memberId;
    private List<String> purposes;
    private List<String> diseases;
    private List<String> healthIssues;
    private List<String> necessaryMeals;
    private List<String> necessaryNutrients;
    private List<String> avoidFoods;
}
