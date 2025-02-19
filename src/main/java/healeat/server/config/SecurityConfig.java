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
import healeat.server.service.authService.CustomOAuth2UserService;
import healeat.server.apiPayload.exception.handler.OAuth2LoginSuccessHandler;
import healeat.server.apiPayload.exception.handler.OAuth2LoginFailureHandler;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import healeat.server.repository.MemberRepository;
import healeat.server.user.JwtTokenProvider;
import healeat.server.user.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import healeat.server.service.authService.LogoutService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final LogoutService logoutService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                //JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, memberRepository, logoutService),
                        UsernamePasswordAuthenticationFilter.class)
                // 인증 필터 순서 조정 - 권한 검사를 먼저 실행
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/naver", "/auth/kakao", "/auth/apple").permitAll()
                        .requestMatchers("/auth/naver/unlink", "/auth/kakao/unlink", "/auth/apple/unlink", "/auth/logout").authenticated()
                        .requestMatchers("/plans/**", "/home/**", "/info/**", "/my-page/**",
                                "/search/**", "/stores/**", "/bookmarks/**","/terms/**").permitAll()
                        .requestMatchers("/profile-image").permitAll()
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
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //JWT 인증을 사용하기 때문에 세션 정책을 STATELESS로 변경
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) //OAuth2 사용자 정보를 DB에 저장
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    //순환 의존성 방지로 OAuth2LoginSuccessHandler는 SecurityConfig에서 직접 빈으로 관리
    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        return new OAuth2LoginSuccessHandler(authorizedClientService, memberRepository, jwtTokenProvider);
    }
}

