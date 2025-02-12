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
import healeat.server.service.authService.UnlinkService;
import healeat.server.web.dto.authResonse.UnlinkResponseDto;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoUnlinkController {

    private final UnlinkService unlinkService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "카카오 회원 탈퇴")
    @PostMapping("/unlink")
    public ResponseEntity<?> unlinkKakao(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // Authorization 헤더 유효성 검사
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Authorization 헤더가 없거나 잘못된 형식입니다.");
            return ResponseEntity.badRequest().body("유효한 액세스 토큰이 필요합니다.");
        }

        String accessToken = authorizationHeader.substring(7).trim();
        System.out.println("클라이언트에서 받은 액세스 토큰: " + accessToken);

        // 카카오 사용자 ID 조회
        String kakaoUserId = getKakaoUserId(accessToken);
        if (kakaoUserId == null) {
            System.out.println("액세스 토큰으로 사용자 정보를 조회할 수 없습니다.");
            return ResponseEntity.status(401).body("카카오 액세스 토큰이 유효하지 않습니다.");
        }
        System.out.println("카카오 사용자 ID: " + kakaoUserId);

        // 카카오 API를 사용하여 탈퇴 요청
        boolean unlinkSuccess = requestKakaoUnlink(accessToken);
        if (!unlinkSuccess) {
            System.out.println("카카오 회원 탈퇴 요청 실패");
            return ResponseEntity.status(500).body("카카오 회원 탈퇴 실패");
        }

        // DB에서 사용자 삭제
        unlinkService.deleteKakaoMember("kakao", kakaoUserId);
        System.out.println("DB에서 사용자 삭제 완료: " + kakaoUserId);

        // 5️⃣ 성공 응답 반환
        return ResponseEntity.ok(
                new UnlinkResponseDto(true, "200", "회원 탈퇴 성공")
        );
    }

    // 카카오 회원 탈퇴 요청
    private boolean requestKakaoUnlink(String accessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            //System.out.println("탈퇴 요청 URL: " + kakaoUnlinkUrl);
            System.out.println("Authorization 헤더: " + headers.get("Authorization"));

            ResponseEntity<String> response = restTemplate.exchange(kakaoUnlinkUrl, HttpMethod.POST, request, String.class);
            System.out.println("탈퇴 요청 응답 상태 코드: " + response.getStatusCode());
            System.out.println("탈퇴 요청 응답 본문: " + response.getBody());

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("탈퇴 요청 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 카카오 사용자 정보 조회
    private String getKakaoUserId(String accessToken) {
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            //System.out.println("사용자 정보 조회 URL: " + kakaoUserInfoUrl);
            //System.out.println("Authorization 헤더: " + headers.get("Authorization"));

            ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, request, Map.class);
            //System.out.println("사용자 정보 조회 응답: " + response.getBody());

            return response.getBody() != null ? response.getBody().get("id").toString() : null;
        } catch (Exception e) {
            System.out.println("사용자 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}


