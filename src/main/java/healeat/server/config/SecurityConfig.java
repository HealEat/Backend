package healeat.server.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import healeat.server.service.CustomOAuth2UserService;
import healeat.server.apiPayload.exception.handler.OAuth2LoginSuccessHandler;
import healeat.server.apiPayload.exception.handler.OAuth2LoginFailureHandler;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import healeat.server.repository.MemberRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 인증 필터 순서 조정 - 권한 검사를 먼저 실행
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/naver", "/auth/kakao", "/auth/apple").permitAll()
                        .requestMatchers("/auth/naver/unlink", "/auth/kakao/unlink", "/auth/apple/unlink").authenticated()
                        .requestMatchers("/plans/**", "/home/**", "/info/**", "/my-page/**", "/search/**", "/stores/**", "/bookmarks/**","/terms/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication is required\"}");
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) //세션 유지
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) //OAuth2 사용자 정보를 DB에 저장
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        // CORS 및 헤더 관련 설정 추가 (헤더가 필터링되지 않도록)
        //http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    //순환 의존성 방지로 OAuth2LoginSuccessHandler는 SecurityConfig에서 직접 빈으로 관리
    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, MemberRepository memberRepository) {
        return new OAuth2LoginSuccessHandler(authorizedClientService, memberRepository);
    }
}



/*
    // SecurityFilterChain의 순서를 조정하기 위한 설정
    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/plans/**", "/home/**", "/info/**", "/my-page/**", "/search/**", "/stores/**","/bookmarks/**",
                        "/swagger-ui/**", "/v3/api-docs/**")
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

} */