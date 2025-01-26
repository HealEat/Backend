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
}