package healeat.server.web.controller;

import healeat.server.service.AppleAuthService;
import healeat.server.web.dto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import healeat.server.web.dto.AppleAuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import healeat.server.service.AppleTokenService;
import healeat.server.user.AppleJwtUtils;
import healeat.server.repository.MemberRepository;
import healeat.server.domain.Member;
import jakarta.servlet.http.HttpSession;
import healeat.server.user.CustomUserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleAuthController {

    private final AppleAuthService appleAuthService;
    private final AppleTokenService appleTokenService;
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Operation(
            summary = "애플 로그인 API",
            description = "authorizationCode 보내면, accessToken과 refreshToken,  providerId을 발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{ \"accessToken\": \"string\", \"refreshToken\": \"string\", \"providerId\": \"string\" }")))
            }
    )


    @PostMapping("/apple")
    public ResponseEntity<?> loginWithApple(@RequestBody AppleAuthRequest request) {
        String authorizationCode = request.getAuthorizationCode();

        // 애플 OAuth 서버에서 액세스 토큰 요청
        AppleTokenResponse tokenResponse = appleAuthService.getAppleAccessToken(authorizationCode);
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        String idToken = tokenResponse.getIdToken();

        // 애플 ID 토큰에서 providerId 추출
        String providerId = AppleJwtUtils.parseIdToken(idToken).getSub();

        // 기존 사용자 조회 or 새 사용자 저장
        Member member = memberRepository.findByProviderAndProviderId("apple", providerId)
                .map(existingMember -> {
                    existingMember.updateRefreshToken(refreshToken);
                    return memberRepository.save(existingMember);
                })
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .provider("apple")
                            .providerId(providerId)
                            .name("AppleUser") // 기본 이름 설정 (애플은 이름 제공 안 함)
                            .refreshToken(refreshToken)
                            .build();
                    return memberRepository.save(newMember);
                });

        // SecurityContextHolder에 사용자 정보 저장 (애플 로그인도 Spring Security 인증 처리)
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(member, Map.of());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 세션에 사용자 저장
        httpSession.setAttribute("user", member);

        System.out.println(" 애플 로그인 성공 - SecurityContextHolder & 세션 저장 완료");

        return ResponseEntity.ok(Map.of(
                "accessToken", tokenResponse.getAccessToken(),
                "refreshToken", tokenResponse.getRefreshToken(),
                "providerId", providerId
        ));
    }

    /*
    @PostMapping("/apple/refresh")
    public ResponseEntity<?> refreshAppleToken(@RequestBody Map<String, String> requestBody) {
        String providerId = requestBody.get("providerId");

        // refreshToken을 이용하여 새로운 accessToken 요청
        String newAccessToken = appleTokenService.refreshAppleAccessToken(providerId);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
     */
}
