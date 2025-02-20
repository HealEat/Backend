package healeat.server.web.controller.authController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import healeat.server.service.authService.LogoutService;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.user.JwtTokenProvider;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LogoutService logoutService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Operation(summary = "카카오 로그인 리다이렉션", description = "카카오 로그인 경로(/oauth2/authorization/kakao)로 리다이렉션(스웨거 테스트 X)")
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @Operation(summary = "네이버 로그인 리다이렉션", description = "네이버 로그인 경로(/oauth2/authorization/naver)로 리다이렉션(스웨거 테스트 X)")
    @GetMapping("/naver")
    public void redirectToNaver(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/naver");
    }



    @Operation(summary = "로그아웃", description = "사용자 로그아웃 (기존 소셜 액세스 토큰 삭제)")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LogoutResponse.class)))

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
            Optional<Member> memberOpt = memberRepository.findById(memberId);

            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();

                // OAuth2AuthorizedClientService 전달하여 강제 삭제
                logoutService.logout(token, member.getProvider(), member.getProviderId(), authorizedClientService, memberRepository);
                member.updateSocialAccessToken(null); // DB에서도 삭제
                memberRepository.save(member);
            }
        }

        // Spring Security 컨텍스트 초기화
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new LogoutResponse(true, "COMMON200", "로그아웃 성공"));
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public record LogoutResponse(boolean isSuccess, String code, String message) {}
}

