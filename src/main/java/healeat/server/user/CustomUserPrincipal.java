package healeat.server.user;

import healeat.server.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;

    public CustomUserPrincipal(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 사용자의 권한 정보를 반환
    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        // 권한 정보가 필요 없으면 빈 리스트를 반환
        return Collections.emptyList();
    }
}
