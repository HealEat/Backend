package healeat.server.web.dto;

import healeat.server.domain.enums.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerRequestDto {
    private List<Answer> selectedAnswers;
}
