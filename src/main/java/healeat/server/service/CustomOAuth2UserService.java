package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.user.OAuth2UserInfo;
import healeat.server.user.OAuth2UserInfoFactory;
import healeat.server.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    @Transactional // 트랜잭션 추가
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //System.out.println("CustomOAuth2UserService.loadUser() 호출됨");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    System.out.println("새로운 사용자 저장: " + name);
                    return memberRepository.save(Member.builder()
                            .provider(oAuth2UserInfo.getProvider())
                            .providerId(providerId)
                            .name(name)
                            .build());
                });

        // 세션에 사용자 정보 저장
        httpSession.setAttribute("user", member);
        System.out.println("사용자 세션 저장 완료: " + member.getName());

        return new CustomUserPrincipal(member, oAuth2User.getAttributes());
    }
}