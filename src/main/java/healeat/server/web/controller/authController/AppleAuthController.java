package healeat.server.web.controller.authController;

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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleAuthController {

    private final AppleAuthService appleAuthService;
    private final AppleTokenService appleTokenService;

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

        AppleTokenResponse tokenResponse = appleAuthService.getAppleAccessToken(authorizationCode);


        // providerId 찾기
        String providerId = AppleJwtUtils.parseIdToken(tokenResponse.getIdToken()).getSub();

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
