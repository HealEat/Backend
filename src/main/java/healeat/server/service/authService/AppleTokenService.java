package healeat.server.service.authService;

import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.user.AppleClientSecretGenerator;
import healeat.server.web.dto.authDto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Optional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

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

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디를 URL-encoded 형식으로 변환
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleClientSecretGenerator.getClientId());
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(
                APPLE_TOKEN_URL,
                requestEntity,
                AppleTokenResponse.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("애플 액세스 토큰 갱신 실패: 응답이 null입니다.");
        }

        return response.getBody().getAccessToken();
    }
}

