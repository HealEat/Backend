package healeat.server.user;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간 (고정)
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일 (고정)

    // application.yml에서 `jwt.secret` 불러오기
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 액세스 토큰 생성 (1시간 유효)
    public String generateAccessToken(Long memberId) {
        return generateToken(memberId, ACCESS_TOKEN_EXPIRATION);
    }

    // 리프레시 토큰 생성 (7일 유효)
    public String generateRefreshToken(Long memberId) {
        return generateToken(memberId, REFRESH_TOKEN_EXPIRATION);
    }

    // JWT 토큰 생성 공통 메서드
    private String generateToken(Long memberId, long expirationTime) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public Long getMemberIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
