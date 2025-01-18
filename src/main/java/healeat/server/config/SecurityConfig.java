package healeat.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import healeat.server.service.CustomOAuth2UserService;
import healeat.server.apiPayload.exception.handler.OAuth2LoginSuccessHandler;
import healeat.server.apiPayload.exception.handler.OAuth2LoginFailureHandler;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          OAuth2LoginFailureHandler oAuth2LoginFailureHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.oAuth2LoginFailureHandler = oAuth2LoginFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(authz -> authz
                        // Swagger 관련 요청 허용
                        .requestMatchers(
                                "/swagger-ui/**",      // Swagger UI 경로
                                "/v3/api-docs/**",     // OpenAPI 문서 경로
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/auth/naver", "/auth/kakao", "/auth/apple").permitAll()  // 소셜 로그인은 인증 없이 접근
                        .requestMatchers("/auth/naver/unlink", "/auth/kakao/unlink", "/auth/apple/unlink").authenticated()  // 탈퇴는 인증 필요
                        .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // CustomOAuth2UserService
                        .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 핸들러
                        .failureHandler(oAuth2LoginFailureHandler) // 로그인 실패 핸들러
                );


        return http.build();
    }
}