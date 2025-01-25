package healeat.server.converter;

import healeat.server.domain.MemberHealQuestion;
import healeat.server.web.dto.HealInfoResponseDto;

public class MemberHealQuestionConverter {

    public static HealInfoResponseDto.BaseResultDto toBaseQuestionDto(MemberHealQuestion memberHealQuestion) {

        return HealInfoResponseDto.BaseResultDto.builder()
                .memberId(memberHealQuestion.getMember().getId())
                .question(memberHealQuestion.getQuestion())
                .savedAnswers(memberHealQuestion.getAnswers())
                .build();
    }
}
