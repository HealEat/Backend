package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.web.dto.ProfileImageResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageConverter {

    public ProfileImageResponseDto toResponseDto(Member member) {
        return ProfileImageResponseDto.builder()
                .id(member.getId())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
