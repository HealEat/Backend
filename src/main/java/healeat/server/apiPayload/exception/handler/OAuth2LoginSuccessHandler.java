package healeat.server.apiPayload.exception.handler;


import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import healeat.server.user.JwtTokenProvider;


//@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.authorizedClientService = authorizedClientService;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println(" 로그인 성공!");

        // 로그인된 사용자 정보 확인
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("OAuth2User 정보: " + oauth2User.getAttributes());


        // 현재 로그인된 사용자의 provider(Kakao, Naver) 가져오기
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        //  provider에 따라 providerId 가져오는 방식 다르게 처리해서 member 객체 가져오기
        String providerId;
        if ("naver".equals(provider)) {
            Map<String, Object> responseMap = (Map<String, Object>) oauth2User.getAttributes().get("response");
            providerId = responseMap.get("id").toString();
        } else {
            providerId = oauth2User.getAttributes().get("id").toString();
        }

        //System.out.println(" provider: " + provider + ", providerId: " + providerId);

        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(provider, providerId);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            //System.out.println(" 현재 로그인한 사용자: " + member.getName());

            // 기존 소셜 로그인 액세스 토큰 가져오기
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, oauthToken.getName());
            if (authorizedClient != null) {
                String socialAccessToken = authorizedClient.getAccessToken().getTokenValue();
                System.out.println("[소셜 로그인 액세스 토큰] : " + socialAccessToken);

                // 소셜 액세스 토큰 DB에 저장
                member.updateSocialAccessToken(socialAccessToken);
                memberRepository.save(member);
                System.out.println("소셜 로그인 액세스 토큰 저장 완료");
            } else {
                System.out.println("소셜 액세스 토큰을 찾을 수 없음.");
            }

            // 자체 JWT 발급 (새로운 액세스 토큰 및 리프레시 토큰 생성)
            String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

            System.out.println("[서버 자체 발급 Access Token] : " + accessToken);
            System.out.println("[서버 자체 발급 Refresh Token] : " + refreshToken);

            // 자체 리프레시 토큰을 DB에 저장
            member.updateRefreshToken(refreshToken);
            memberRepository.save(member);

            // 클라이언트 앱으로 리다이렉트 (커스텀 스킴 사용) - JWT 전달
            String redirectUrl = "com.umc7.healeat://?message=LoginSuccessful&accessToken=" + accessToken;
            System.out.println("리다이렉트 URL: " + redirectUrl);
            response.sendRedirect(redirectUrl);
        } else {
            System.out.println("OAuth2 로그인 성공했지만 Member를 찾을 수 없음.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
        }
    }
}
