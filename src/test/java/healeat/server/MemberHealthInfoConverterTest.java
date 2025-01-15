package healeat.server;

import healeat.server.converter.MemberHealthInfoConverter;
import healeat.server.domain.HealthInfoAnswer;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Question;
import healeat.server.web.dto.AnswerResponseDto;
import healeat.server.web.dto.QuestionResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MemberHealthInfoConverterTest {
    @Autowired
    private MemberHealthInfoConverter converter;

    @Test
    void testToQuestionResponseDto() {
        MemberHealQuestion question = MemberHealQuestion.builder()
                .id(1L)
                .question(Question.HEALTH_ISSUE)
                .healthInfoAnswers(List.of(
                        HealthInfoAnswer.builder().answer(Answer.WEIGHT_LOSS).build(),
                        HealthInfoAnswer.builder().answer(Answer.INDIGESTION).build()
                ))
                .build();

        QuestionResponseDto dto = converter.toQuestionResponseDto(question);

        assertEquals(1L, dto.getQuestionId());
        assertEquals("HEALTH_ISSUE", dto.getQuestionText());
        assertEquals(List.of(Answer.WEIGHT_LOSS, Answer.INDIGESTION), dto.getAnswers());
    }

    @Test
    void testToAnswerResponseDto() {
        AnswerResponseDto dto = converter.toAnswerResponseDto(1L, 2L, List.of(Answer.LOW_FAT, Answer.HIGH_VEGETABLE));

        assertEquals(1L, dto.getMemberId());
        assertEquals(2L, dto.getQuestionId());
        assertEquals(List.of(Answer.LOW_FAT, Answer.HIGH_VEGETABLE), dto.getSelectedOptions());
    }
}