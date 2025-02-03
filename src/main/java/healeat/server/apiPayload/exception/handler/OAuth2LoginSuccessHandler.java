package healeat.server.apiPayload.exception.handler;

//import org.springframework.stereotype.Component;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;


//@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberRepository memberRepository;

    public OAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, MemberRepository memberRepository) {
        this.authorizedClientService = authorizedClientService;
        this.memberRepository = memberRepository;
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

        System.out.println("✅ provider: " + provider + ", providerId: " + providerId);

        Optional<Member> memberOpt = memberRepository.findByProviderAndProviderId(provider, providerId);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            System.out.println("✅ 현재 로그인한 사용자: " + member.getName());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    member, null, new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            System.out.println("OAuth2 로그인 성공했지만 Member를 찾을 수 없음.");
        }

        // 액세스 토큰 가져오기
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, oauthToken.getName());

        if (authorizedClient != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            System.out.println("추출된 액세스 토큰: " + accessToken);

            // JSON 응답 반환 (액세스 토큰 포함)
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login Successful");
            responseBody.put("accessToken", accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } else {
            System.out.println("액세스 토큰을 찾을 수 없음");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token not found");
        }
        /* 액세스 토큰을 클라이언트에게 반환하는 것을 추가하면서 삭제
        // 로그인 성공 후 처리 로직
        String redirectUrl = "/home"; // 로그인 후 리다이렉트할 URL
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);*/
    }
}
