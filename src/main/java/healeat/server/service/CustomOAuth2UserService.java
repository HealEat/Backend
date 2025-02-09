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

import healeat.server.user.AppleUserInfo;
import healeat.server.user.AppleJwtUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    @Transactional // 트랜잭션 추가
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());


        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId;
        String name;

        // 애플 로그인: id_token을 파싱하여 사용자 정보 추출
        if ("apple".equals(provider)) {
            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            AppleUserInfo appleUserInfo = AppleJwtUtils.parseIdToken(idToken);
            providerId = appleUserInfo.getSub();  // 애플 사용자 고유 ID
            name = "AppleUser"; // 애플은 기본적으로 이름 제공 안 하므로 임시 이름 설정
        } else {
            // 카카오/네이버 로그인: OAuth2UserInfo 사용
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
            providerId = oAuth2UserInfo.getProviderId();
            name = oAuth2UserInfo.getName();
        }

        //애플 로그인일 경우 refresh_token 저장
        final String finalRefreshToken;
        if ("apple".equals(provider)) {
            finalRefreshToken = userRequest.getAccessToken().getTokenValue();
        } else {
            finalRefreshToken = null; // 카카오/네이버는 refresh_token 저장 X
        }


        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingMember -> {
                    if ("apple".equals(provider) && finalRefreshToken != null) {
                        existingMember.updateRefreshToken(finalRefreshToken);
                    }
                    return memberRepository.save(existingMember);
                })
                .orElseGet(() -> {
                    System.out.println("새로운 사용자 저장: " + name);
                    return memberRepository.save(Member.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .name(name)
                            .refreshToken(finalRefreshToken)  //애플 로그인일 경우에만 저장됨
                            .build());
                });

        // 세션에 사용자 정보 저장
        httpSession.setAttribute("user", member);
        System.out.println("사용자 세션 저장 완료: " + member.getName());

        return new CustomUserPrincipal(member, oAuth2User.getAttributes());
    }
}

