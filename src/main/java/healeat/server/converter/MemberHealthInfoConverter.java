package healeat.server.converter;

import healeat.server.domain.HealthInfoAnswer;
import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.domain.enums.Answer;
import healeat.server.web.dto.AnswerResponseDto;
import healeat.server.web.dto.MemberHealthInfoResponseDto;
import healeat.server.web.dto.QuestionResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberHealthInfoConverter {

    public MemberHealthInfoResponseDto toMemberHealthInfoResponseDto(Member member, List<QuestionResponseDto> questions) {
        return MemberHealthInfoResponseDto.builder()
                .memberId(member.getId())
                .questions(questions)
                .build();
    }

    public QuestionResponseDto toQuestionResponseDto(MemberHealQuestion question) {
        return QuestionResponseDto.builder()
                .questionId(question.getId())
                .questionText(question.getQuestion().name())
                .answers(question.getHealthInfoAnswers().stream()
                        .map(HealthInfoAnswer::getAnswer)
                        .toList())
                .build();
    }

    public AnswerResponseDto toAnswerResponseDto(Long memberId, Integer questionId, List<Answer> selectedAnswers) {
        return AnswerResponseDto.builder()
                .memberId(memberId)
                .questionId(questionId)
                .selectedAnswers(selectedAnswers)
                .build();
    }

    public List<HealthInfoAnswer> toHealthInfoAnswers(List<Answer> answers, MemberHealQuestion question) {
        return answers.stream()
                .map(answer -> HealthInfoAnswer.builder()
                        .memberHealQuestion(question)
                        .answer(answer)
                        .build())
                .toList();
    }
}
