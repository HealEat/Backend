package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class AppleAuthRequest {
    private String authorizationCode;
}
