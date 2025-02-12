package healeat.server.web.dto.authResonse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor


public class AppleTokenResponse {

    @JsonProperty("access_token")
    String accessToken;  // 애플 OAuth2 access token

    @JsonProperty("expires_in")
    Integer expiresIn;  // access_token 유효 시간 (초 단위)

    @JsonProperty("id_token")
    String idToken;  // 사용자 정보가 포함된 JWT 토큰

    @JsonProperty("refresh_token")
    String refreshToken;  // refresh token (재로그인 시 사용)

    @JsonProperty("token_type")
    String tokenType;  // "Bearer" (고정 값)

    @JsonProperty("error")
    String error;  // 오류 발생 시 에러 메시지


}

