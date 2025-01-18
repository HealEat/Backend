package healeat.server.user;

import java.util.Map;
//각 소셜 로그인 제공자에 맞는 OAuth2UserInfo 객체를 반환

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth provider: " + provider);
        };
    }
}