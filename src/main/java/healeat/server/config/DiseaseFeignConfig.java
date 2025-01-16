package healeat.server.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiseaseFeignConfig {

    @Value("${disease.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor diseaseRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.query("serviceKey", apiKey);
        };
    }
}
