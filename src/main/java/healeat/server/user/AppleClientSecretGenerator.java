package healeat.server.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final String teamId;
    private final String clientId;
    private final String keyId;
    private final Resource privateKeyResource;

    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

    public String generateClientSecret() {
        try {
            PrivateKey privateKey = getPrivateKey();
            long now = System.currentTimeMillis() / 1000; // 현재 시간 (초 단위)
            long exp = now + 2505600; // 29일(= 29 * 24 * 60 * 60초) 유효기간 설정

            return Jwts.builder()
                    .setHeaderParam("kid", keyId)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(teamId)
                    .setIssuedAt(new Date(now * 1000))
                    .setExpiration(new Date(exp * 1000))
                    .setAudience(APPLE_AUDIENCE)
                    .setSubject(clientId)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("애플 OAuth client_secret 생성 실패", e);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(privateKeyResource.getFile().toPath());
        String keyString = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
