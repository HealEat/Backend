package healeat.server.apiPayload.exception.handler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println(" 로그인 성공!"); // 디버깅 로그 추가

        // 로그인된 사용자 정보 확인
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("OAuth2User 정보: " + oauth2User.getAttributes());

        // 로그인 성공 후 처리 로직
        String redirectUrl = "/home"; // 로그인 후 리다이렉트할 URL
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}