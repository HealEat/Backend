package healeat.server.web.dto;

import healeat.server.domain.Member;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Question;
import healeat.server.domain.enums.Vegetarian;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyHealthProfileDto {

        List<String> diseases; // List 가 Null 이면 나의 건강 목표에 질병관리 X
        Vegetarian vegetarian;
        Diet diet;

        List<MyProfileQnaDto> qnas;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProfileQnaDto {

        Question question;
        List<Answer> answers;
    }
}
