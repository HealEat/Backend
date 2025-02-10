package healeat.server.web.controller.authController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import org.springframework.http.MediaType;
import healeat.server.service.UnlinkService;
import healeat.server.web.dto.UnlinkResponseDto;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/auth/naver")
@RequiredArgsConstructor
public class NaverUnlinkController {

    private final UnlinkService unlinkService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Operation(summary = "네이버 회원 탈퇴")
    @PostMapping("/unlink")
    public ResponseEntity<?> unlinkNaver(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // Authorization 헤더 유효성 검사
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("유효한 액세스 토큰이 필요합니다.");
        }

        String accessToken = authorizationHeader.substring(7).trim();
        System.out.println("클라이언트에서 받은 액세스 토큰: " + accessToken);

        // 네이버 사용자 ID 조회
        String naverUserId = getNaverUserId(accessToken);
        if (naverUserId == null) {
            return ResponseEntity.status(401).body("네이버 액세스 토큰이 유효하지 않습니다.");
        }

        // 네이버 API를 사용하여 탈퇴 요청
        boolean unlinkSuccess = requestNaverUnlink(accessToken);
        if (!unlinkSuccess) {
            return ResponseEntity.status(500).body("네이버 회원 탈퇴 실패");
        }

        // DB에서 사용자 삭제
        unlinkService.deleteKakaoMember("naver", naverUserId);

        // 성공 응답 반환
        return ResponseEntity.ok(
                new UnlinkResponseDto(true, "200", "회원 탈퇴 성공")
        );
    }

    // 네이버 회원 탈퇴 요청
    private boolean requestNaverUnlink(String accessToken) {
        String naverUnlinkUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "delete");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("access_token", accessToken);
        body.add("service_provider", "NAVER");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(naverUnlinkUrl, HttpMethod.POST, request, Map.class);
            return response.getStatusCode().is2xxSuccessful() && "success".equals(response.getBody().get("result"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 네이버 사용자 정보 조회
    private String getNaverUserId(String accessToken) {
        String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(naverUserInfoUrl, HttpMethod.GET, request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("response")) {
                Map<String, Object> userInfo = (Map<String, Object>) response.getBody().get("response");
                return userInfo.get("id").toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


