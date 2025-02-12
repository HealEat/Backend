package healeat.server.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsAgreeRequest {

    List<TermAgree> agreements;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermAgree {

        @NotNull
        Long termId;
        @NotNull
        Boolean agree;
    }
}
