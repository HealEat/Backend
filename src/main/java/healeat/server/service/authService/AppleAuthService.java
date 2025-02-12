package healeat.server.service.authService;

import healeat.server.web.dto.authResonse.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import healeat.server.user.AppleClientSecretGenerator;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor

public class AppleAuthService {
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final @Qualifier("restTemplate") RestTemplate restTemplate;  // 2.9 오류 때문에 추가

    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";

    public AppleTokenResponse getAppleAccessToken(String authorizationCode) {
        String clientSecret = appleClientSecretGenerator.generateClientSecret();

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("client_id", "com.example.app");
        requestParams.put("client_secret", clientSecret);
        requestParams.put("code", authorizationCode);
        requestParams.put("grant_type", "authorization_code");

        ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(
                APPLE_TOKEN_URL,
                new HttpEntity<>(requestParams),
                AppleTokenResponse.class
        );

        return response.getBody();
    }
}
