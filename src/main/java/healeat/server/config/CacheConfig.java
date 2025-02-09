package healeat.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching  // 캐싱 기능 활성화
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "searchResult", "recommendResult", "itemDaumImages");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000)  // 최대 10,000개 저장
                .expireAfterWrite(1440, TimeUnit.MINUTES)  // 24시간 후 만료
                .recordStats());  // 캐시 통계 활성화
        return cacheManager;
    }
}
