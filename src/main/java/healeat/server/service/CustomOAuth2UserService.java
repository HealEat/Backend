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

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2User를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // provider 정보 ( kakao, naver, apple)
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getName(); // OAuth2 제공자의 고유 ID

        // 사용자 정보 매핑 (이름만 가져오기)
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        String name = oAuth2UserInfo.getName();

        // DB에서 사용자 찾기 또는 신규 사용자 생성
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // 새로운 사용자일 경우 회원가입 처리
                    Member newMember = Member.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .name(name)
                            .build();
                    return memberRepository.save(newMember);
                });

        // 사용자 정보를 CustomUserPrincipal에 전달
        return new CustomUserPrincipal(member, oAuth2User.getAttributes());
    }
}
