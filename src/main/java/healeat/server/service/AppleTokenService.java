package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.user.AppleClientSecretGenerator;
import healeat.server.web.dto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor

// 애플 accessToken 갱신
public class AppleTokenService {
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";

    public String refreshAppleAccessToken(String providerId) {
        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId("apple", providerId);

        if (memberOpt.isEmpty() || memberOpt.get().getRefreshToken() == null) {
            throw new RuntimeException("해당 사용자의 refresh_token이 없습니다.");
        }

        String refreshToken = memberOpt.get().getRefreshToken();
        String clientSecret = appleClientSecretGenerator.generateClientSecret();

        Map<String, String> params = new HashMap<>();
        params.put("client_id", "com.example.app");
        params.put("client_secret", clientSecret);
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");

        ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(
                APPLE_TOKEN_URL,
                new HttpEntity<>(params),
                AppleTokenResponse.class
        );

        return response.getBody().getAccessToken();
    }
}

