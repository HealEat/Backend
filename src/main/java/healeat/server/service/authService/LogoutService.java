package healeat.server.service.authService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 12; // JWT 만료 시간 (12시간)

    public void logout(String token) {
        // JWT 만료 시간을 가져와 Redis에 저장 (만료 시간까지 유지)
        long expiration = JWT_EXPIRATION_TIME;
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isLoggedOut(String token) {
        return redisTemplate.hasKey(token);
    }
}
