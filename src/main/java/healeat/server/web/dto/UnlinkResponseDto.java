package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnlinkResponseDto {
    private boolean isSuccess;
    private String code;
    private String message;
}

