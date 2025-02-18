package healeat.server.service.authService;

import healeat.server.user.AppleClientSecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class AppleUnlinkService {
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final RestTemplate restTemplate;

    private static final String APPLE_UNLINK_URL = "https://appleid.apple.com/auth/revoke";

    public boolean unlinkAppleAccount(String providerId, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("해당 사용자의 refresh_token이 없습니다.");
        }

        String clientSecret = appleClientSecretGenerator.generateClientSecret();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디를 URL-encoded 형식으로 변환
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleClientSecretGenerator.getClientId());
        params.add("client_secret", clientSecret);
        params.add("token", refreshToken);
        params.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    APPLE_UNLINK_URL,
                    requestEntity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("애플 회원 탈퇴 실패: " + e.getMessage(), e);
        }
    }
}
