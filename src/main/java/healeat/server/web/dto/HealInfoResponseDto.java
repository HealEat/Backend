package healeat.server.web.dto;

import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HealInfoResponseDto {

    Long memberId;
    List<String> healEatFoods;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChooseResultDto {

        Long memberId;
        String choose;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeChoiceResultDto {

        ChooseResultDto choseResult;
        List<String> healEatFoods;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BaseResultDto {

        Long memberId;
        Question question;
        List<Answer> savedAnswers;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeBaseResultDto {

        BaseResultDto baseResultDto;
        List<String> healEatFoods;
    }

    /// 마이페이지 - 나의 건강 정보 조회 dto
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyHealthInfoDto {

        List<String> healthGoals;   // 건강 목표 (질병 관리, 비건, 다이어트)
        String vegetarianType;      // 비건 종류
        List<String> healthIssues;  // 건강 상의 불편함
        List<String> requiredMeals; // 필요한 식사
        List<String> requiredNutrients;     // 필요한 영양소
        List<String> avoidedFoods;  // 피해야 하는 음식
    }
}