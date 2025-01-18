package healeat.server.user;

import healeat.server.domain.Member;
import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Map;

public class CustomUserPrincipal extends User implements OAuth2User {
    private Member member;
    private Map<String, Object> attributes;

    public CustomUserPrincipal(Member member, Map<String, Object> attributes) {
        super(member.getName(), "", new ArrayList<>());
        this.member = member;
        this.attributes = attributes;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getName();
    }
}