package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppleUnlinkRequest {
    private String providerId;  // 애플에서 발급한 사용자 고유 ID
}

