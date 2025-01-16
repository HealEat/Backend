package healeat.server.web.dto;

import healeat.server.domain.enums.Answer;
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
    private Long memberId;
    private Integer questionId;
    private List<Long> selectedOptions;
}
