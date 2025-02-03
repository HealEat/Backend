package healeat.server.web.controller;

import healeat.server.repository.MemberRepository;
import healeat.server.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoUnlinkController {
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "카카오 회원 탈퇴")
    @PostMapping("/unlink")
    public ResponseEntity<?> unlinkKakao(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // 클라이언트에서 받은 액세스 토큰 추출
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("유효한 액세스 토큰이 필요합니다.");
        }

        String accessToken = authorizationHeader.substring(7);
        System.out.println("클라이언트에서 받은 액세스 토큰: " + accessToken);

        // 카카오 회원 탈퇴 요청
        boolean unlinkSuccess = requestKakaoUnlink(accessToken);
        if (!unlinkSuccess) {
            return ResponseEntity.status(500).body("카카오 회원 탈퇴 실패");
        }

        // DB에서 사용자 삭제
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof Member member) {
            System.out.println("탈퇴할 사용자: " + member.getName());
            memberRepository.delete(member);
            return ResponseEntity.ok("카카오 회원 탈퇴 성공");
        }

        return ResponseEntity.status(401).body("Unauthorized: 사용자 정보가 없습니다.");
    }

    private boolean requestKakaoUnlink(String accessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(kakaoUnlinkUrl, HttpMethod.POST, request, String.class);
            System.out.println("카카오 API 응답 상태 코드: " + response.getStatusCode());
            System.out.println("카카오 API 응답 본문: " + response.getBody());

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("카카오 회원 탈퇴 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

