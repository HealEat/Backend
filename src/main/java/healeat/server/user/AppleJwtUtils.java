package healeat.server.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

public class AppleJwtUtils {
    public static AppleUserInfo parseIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String payload = new String(Base64.getDecoder().decode(parts[1]));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            String sub = jsonNode.get("sub").asText(); // 애플의 고유 사용자 ID
            String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;

            return new AppleUserInfo(sub, email);
        } catch (Exception e) {
            throw new RuntimeException("애플 ID 토큰 파싱 실패: " + e.getMessage(), e);
        }
    }
}

