package healeat.server.service.authService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 12; // JWT 만료 시간 (12시간)

    @Transactional
    public void logout(String token, String provider, String principalName, OAuth2AuthorizedClientService authorizedClientService, MemberRepository memberRepository) {
        // JWT 블랙리스트 추가
        redisTemplate.opsForValue().set(token, "logout", JWT_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        //기존 OAuth2 클라이언트 삭제
        authorizedClientService.removeAuthorizedClient(provider, principalName);

        //DB에서 소셜 액세스 토큰 삭제
        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(provider, principalName);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.updateSocialAccessToken(null); // 네이버 액세스 토큰 삭제
            memberRepository.save(member);
        }
    }

    public boolean isLoggedOut(String token) {
        return redisTemplate.hasKey(token);
    }
}

