package healeat.server.user;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }
}