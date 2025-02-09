package healeat.server.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleUserInfo {
    private String sub;  // 애플의 고유 사용자 ID
    private String email;  // 애플에서 제공하는 이메일 (nullable)
}

