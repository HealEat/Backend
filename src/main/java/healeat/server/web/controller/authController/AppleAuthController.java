package healeat.server.web.controller.authController;

import healeat.server.service.authService.AppleAuthService;
import healeat.server.web.dto.authDto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import healeat.server.web.dto.authDto.AppleAuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import healeat.server.service.authService.AppleTokenService;
import healeat.server.user.AppleJwtUtils;
import healeat.server.repository.MemberRepository;
import healeat.server.domain.Member;
import jakarta.servlet.http.HttpSession;
import healeat.server.service.authService.AppleUnlinkService;
import healeat.server.user.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleAuthController {

    private final AppleAuthService appleAuthService;
    private final AppleTokenService appleTokenService;
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final AppleUnlinkService appleUnlinkService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "애플 로그인 API",
            description = "authorizationCode 보내면,   accessToken과 providerId을 발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{  \"accessToken\": \"string\", \"providerId\": \"string\" }")))
            }
    )


    @PostMapping("/apple")
    public ResponseEntity<?> loginWithApple(@RequestBody AppleAuthRequest request) {
        String authorizationCode = request.getAuthorizationCode();

        // 애플 OAuth 서버에서 액세스 토큰 요청
        AppleTokenResponse tokenResponse = appleAuthService.getAppleAccessToken(authorizationCode);
        String appleAccessToken = tokenResponse.getAccessToken();
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
                            .name("AppleUser") // 기본 이름 설정
                            .refreshToken(refreshToken)
                            .socialAccessToken(appleAccessToken)
                            .build();
                    return memberRepository.save(newMember);
                });

        // 자체 JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        // 기존 리프레시 토큰을 갱신하여 저장
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        // JWT 반환 (애플의 액세스 토큰 대신 서버 JWT 사용)
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,   // 서버 JWT 반환
                //"refreshToken", newRefreshToken,  // 서버에서 발급한 리프레시 토큰 반환
                "providerId", providerId
        ));
    }

    @Operation(summary = "애플 회원 탈퇴", description = " 액세스 토큰 헤더로 보내면, 애플 계정을 해제하고 서버에서 삭제")
    @PostMapping("/apple/unlink")
    public ResponseEntity<?> unlinkAppleAccount(HttpServletRequest request) {
        String token = resolveToken(request); //서버 자체 JWT 토큰 확인

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }

        // JWT에서 사용자 ID 추출
        Long memberId = jwtTokenProvider.getMemberIdFromToken(token);

        // DB에서 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (!"apple".equals(member.getProvider())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "애플 계정이 아닙니다."));
        }

        String refreshToken = member.getRefreshToken();
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "애플 리프레시 토큰이 없음"));
        }

        // 애플 API를 통해 탈퇴 요청
        boolean isUnlinked = appleUnlinkService.unlinkAppleAccount(member.getProviderId(), refreshToken);

        if (isUnlinked) {
            // DB에서 회원 정보 삭제
            memberRepository.deleteById(memberId);
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "애플 회원 탈퇴 실패"));
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
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
