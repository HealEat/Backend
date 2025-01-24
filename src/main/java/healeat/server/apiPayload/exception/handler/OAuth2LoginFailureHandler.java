package healeat.server.apiPayload.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) throws IOException {
        // 실패 로그 기록
        System.out.println("소셜 로그인 실패: " + exception.getMessage());

        // 실패 이유를 쿼리 파라미터로 리다이렉트
        response.sendRedirect("/login?error=" + exception.getMessage());
    }
}
