package healeat.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/auth/naver", "/auth/kakao", "/auth/apple").permitAll()  // 소셜 로그인은 인증 없이 접근
//                        .requestMatchers("/auth/naver/unlink", "/auth/kakao/unlink", "/auth/apple/unlink").authenticated()  // 탈퇴는 인증 필요
//                        .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")  // 로그인 페이지 URL 설정
//                        .defaultSuccessUrl("/home", true)  // 로그인 성공 후 리디렉션 URL
//                        .permitAll()
//                );
//
//
//        return http.build();
//    }
//}

