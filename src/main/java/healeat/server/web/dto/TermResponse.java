package healeat.server.web.dto;

import healeat.server.domain.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermResponse {

    Long id;
    String title;
    String body;
    Boolean isRequired;

    public static TermResponse from(Term term) {
        return new TermResponse(
                term.getId(), term.getTitle(), term.getBody(), term.isRequired());
    }
}
