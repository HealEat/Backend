package healeat.server.apiPayload.exception.handler;

import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository; // DB 조회용

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증된 사용자 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // provider 정보 (OAuth2 로그인 제공자 정보 가져오기)
        String provider = ((OAuth2User) authentication.getPrincipal()).getAttributes().get("registration_id").toString(); // 'registration_id'에서 provider 정보 얻기
        String providerId = oAuth2User.getName();  // OAuth2 제공자의 고유 ID

        // DB에서 사용자 찾기
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        // 인증된 사용자를 SecurityContext에 설정
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member, null, authentication.getAuthorities());

        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 로그인 성공 후 리디렉션
        response.sendRedirect("/home");
    }
}
