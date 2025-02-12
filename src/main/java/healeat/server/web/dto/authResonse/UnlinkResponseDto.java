package healeat.server.web.dto.authResonse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnlinkResponseDto {
    Boolean isSuccess;
    String code;
    String message;
}

