package healeat.server.web.dto;

import healeat.server.domain.Member;
import lombok.Getter;

@Getter
public class MemberProfileResponseDto {

    private String name;
    private String profileImage;

    public static MemberProfileResponseDto from(Member member) {
        MemberProfileResponseDto response = new MemberProfileResponseDto();
        response.name = member.getName();
        response.profileImage = member.getProfileImageUrl();
        return response;
    }
}
