package healeat.server.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final HttpSession httpSession;

    @Operation(summary = "카카오 로그인 리다이렉션", description = "카카오 로그인 경로로 리다이렉션(스웨거 테스트 X)")
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @Operation(summary = "네이버 로그인 리다이렉션", description = "네이버 로그인 경로로 리다이렉션(스웨거 테스트 X)")
    @GetMapping("/naver")
    public void redirectToNaver(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/naver");
    }

    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 세션을 무효화")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공 응답",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LogoutResponse.class)))
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        // 세션 무효화
        httpSession.invalidate();

        // Spring Security 컨텍스트 제거
        SecurityContextHolder.clearContext();

        System.out.println("사용자 로그아웃 성공");

        return ResponseEntity.ok(new LogoutResponse(true, "COMMON200", "로그아웃 성공"));
    }

    public record LogoutResponse(boolean isSuccess, String code, String message) {}
}
