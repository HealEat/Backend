package healeat.server.service;

import healeat.server.repository.MemberRepository;
import healeat.server.user.AppleClientSecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleUnlinkService {
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    private static final String APPLE_UNLINK_URL = "https://appleid.apple.com/auth/revoke";

    public boolean unlinkAppleAccount(String providerId, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("해당 사용자의 refresh_token이 없습니다.");
        }

        String clientSecret = appleClientSecretGenerator.generateClientSecret();

        Map<String, String> params = new HashMap<>();
        params.put("client_id", "sociallogin.healeat.com");  // 애플에 등록한 client_id
        params.put("client_secret", clientSecret);
        params.put("token", refreshToken);
        params.put("token_type_hint", "refresh_token");

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    APPLE_UNLINK_URL,
                    new HttpEntity<>(params),
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            throw new RuntimeException("애플 회원 탈퇴 실패: " + e.getMessage(), e);
        }
    }
}
