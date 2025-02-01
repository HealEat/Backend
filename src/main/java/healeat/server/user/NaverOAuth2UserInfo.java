package healeat.server.user;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return (String) ((Map<String, Object>) attributes.get("response")).get("id");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>) attributes.get("response")).get("name");
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}
