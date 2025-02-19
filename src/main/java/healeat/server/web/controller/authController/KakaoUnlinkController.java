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
import healeat.server.web.dto.authDto.UnlinkResponseDto;
import healeat.server.repository.MemberRepository;
import healeat.server.domain.Member;
import healeat.server.user.JwtTokenProvider;
import java.util.Optional;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoUnlinkController {

    private final UnlinkService unlinkService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "카카오 회원 탈퇴")
    @PostMapping("/unlink")
    public ResponseEntity<?> unlinkKakao(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // 클라이언트가 보낸 JWT 토큰 검증
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("유효한 액세스 토큰이 필요합니다.");
        }

        // 자체 발급된 JWT에서 memberId 추출
        String token = authorizationHeader.substring(7).trim();
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).body("잘못된 JWT 토큰입니다.");
        }
        Long memberId = jwtTokenProvider.getMemberIdFromToken(token);

        //  DB에서 사용자 조회
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            return ResponseEntity.status(404).body("회원 정보를 찾을 수 없습니다.");
        }
        Member member = memberOpt.get();

        //  DB에 저장된 카카오 소셜 액세스 토큰 가져오기
        String socialAccessToken = member.getSocialAccessToken();
        if (socialAccessToken == null || socialAccessToken.isEmpty()) {
            return ResponseEntity.status(401).body("카카오 소셜 액세스 토큰이 없습니다.");
        }

        // 카카오 사용자 ID 조회 (소셜 액세스 토큰 사용)
        String kakaoUserId = getKakaoUserId(socialAccessToken);
        if (kakaoUserId == null) {
            return ResponseEntity.status(401).body("카카오 소셜 액세스 토큰이 유효하지 않습니다.");
        }

        // 카카오 API를 사용하여 회원 탈퇴 요청
        boolean unlinkSuccess = requestKakaoUnlink(socialAccessToken);
        if (!unlinkSuccess) {
            return ResponseEntity.status(500).body("카카오 회원 탈퇴 실패");
        }

        // DB에서 사용자 삭제
        unlinkService.deleteSocialMember("kakao", kakaoUserId);

        // 성공 응답 반환
        return ResponseEntity.ok(
                new UnlinkResponseDto(true, "200", "회원 탈퇴 성공")
        );
    }

    // 카카오 회원 탈퇴 요청 (소셜 액세스 토큰 사용)
    private boolean requestKakaoUnlink(String socialAccessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + socialAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(kakaoUnlinkUrl, HttpMethod.POST, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 카카오 사용자 정보 조회
    private String getKakaoUserId(String socialAccessToken) {
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + socialAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUrl, HttpMethod.GET, request, Map.class);
            if (response.getBody() != null) {
                return response.getBody().get("id").toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



