package healeat.server.web.dto;

import healeat.server.domain.mapping.MemberTerm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTermResponse {
    private Long termId;
    private String title;
    private Boolean agree;

    public static MemberTermResponse from(MemberTerm memberTerm) {
        return new MemberTermResponse(
                memberTerm.getTerm().getId(),
                memberTerm.getTerm().getTitle(),
                memberTerm.getAgree()
        );
    }
}
