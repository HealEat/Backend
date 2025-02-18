package healeat.server.service.authService;

import healeat.server.web.dto.authDto.AppleTokenResponse;
import healeat.server.user.AppleClientSecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor

public class AppleAuthService {
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final @Qualifier("restTemplate") RestTemplate restTemplate;  // 2.9 오류 때문에 추가

    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";

    public AppleTokenResponse getAppleAccessToken(String authorizationCode) {
        String clientSecret = appleClientSecretGenerator.generateClientSecret();

        // 요청 바디를 URL-encoded 형식으로 변환
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", appleClientSecretGenerator.getClientId());
        requestParams.add("client_secret", clientSecret);
        requestParams.add("code", authorizationCode);
        requestParams.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestParams, headers);

        ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(
                APPLE_TOKEN_URL,
                requestEntity,
                AppleTokenResponse.class
        );

        return response.getBody();  //여기서 `id_token`을 받음
    }
}
