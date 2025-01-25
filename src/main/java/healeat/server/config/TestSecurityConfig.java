package healeat.server.config;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@Profile("test")
@RequiredArgsConstructor
public class TestSecurityConfig {

    // 테스트 환경에서만 동작하는 빈 정의
    // 999번 멤버를 위함

    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Filter testAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .addFilterBefore(testAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 필터 추가
        return http.build();
    }

    @Bean
    public Filter testAuthenticationFilter() {
        return (request, response, chain) -> {

            // 999번 멤버를 리포지토리에서 조회
            Member testMember = memberRepository.findById(999L)
                    .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

            // SecurityContext에 인증 정보 주입
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            testMember,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );

            chain.doFilter(request, response);
        };
    }
}
