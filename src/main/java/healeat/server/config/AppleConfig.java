package healeat.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import healeat.server.user.AppleClientSecretGenerator;

@Configuration
public class AppleConfig {

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.private-key-path}")
    private Resource privateKeyResource;

    @Bean
    public AppleClientSecretGenerator appleClientSecretGenerator() {
        return new AppleClientSecretGenerator(teamId, clientId, keyId, privateKeyResource);
    }
}
