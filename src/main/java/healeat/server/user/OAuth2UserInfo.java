package healeat.server.user;

import java.util.Map;

// 각 소셜 로그인 제공자마다 다르게 처리되는 사용자 정보를 동일한 방식으로 다룸
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public abstract String getProviderId();

    public abstract String getName();

    public abstract String getProvider();
}