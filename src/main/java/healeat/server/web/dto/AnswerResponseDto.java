package healeat.server.web.dto;

import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerResponseDto {

    Long memberId;
    Question question;
    List<Answer> selectedAnswers;
}