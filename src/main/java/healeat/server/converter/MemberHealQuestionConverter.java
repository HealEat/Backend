package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.web.dto.HealInfoResponseDto;

public class MemberHealQuestionConverter {

    public static HealInfoResponseDto.ChooseResultDto toChooseVegetResult(Member member) {

        return HealInfoResponseDto.ChooseResultDto.builder()
                .memberId(member.getId())
                .choose(member.getVegetarian().name())
                .build();
    }

    public static HealInfoResponseDto.ChooseResultDto toChooseDietResult(Member member) {

        return HealInfoResponseDto.ChooseResultDto.builder()
                .memberId(member.getId())
                .choose(member.getDiet().name())
                .build();
    }

    public static HealInfoResponseDto.BaseResultDto toBaseResult(MemberHealQuestion memberHealQuestion) {

        Member member = memberHealQuestion.getMember();

        return HealInfoResponseDto.BaseResultDto.builder()
                .memberId(member.getId())
                .question(memberHealQuestion.getQuestion())
                .savedAnswers(memberHealQuestion.getAnswers())
                .build();
    }

//    public static HealInfoResponseDto.ChangeChoiceResultDto toChangeVegetResult(Member member) {
//
//        HealInfoResponseDto.ChooseResultDto result = HealInfoResponseDto.ChooseResultDto.builder()
//                .memberId(member.getId())
//                .choose(member.getVegetarian().name()) // 베지테리언
//                .build();
//
//        return HealInfoResponseDto.ChangeChoiceResultDto.builder()
//                .choseResult(result)
//                .healEatFoods(member.getHealEatFoods())
//                .build();
//    }
//
//    public static HealInfoResponseDto.ChangeChoiceResultDto toChangeDietResult(Member member) {
//
//        HealInfoResponseDto.ChooseResultDto result = HealInfoResponseDto.ChooseResultDto.builder()
//                .memberId(member.getId())
//                .choose(member.getDiet().name()) // 다이어트
//                .build();
//
//        return HealInfoResponseDto.ChangeChoiceResultDto.builder()
//                .choseResult(result)
//                .healEatFoods(member.getHealEatFoods())
//                .build();
//    }
//
//    public static HealInfoResponseDto.ChangeBaseResultDto toChangeBaseResult(MemberHealQuestion memberHealQuestion) {
//
//        Member member = memberHealQuestion.getMember();
//
//        HealInfoResponseDto.BaseResultDto result = HealInfoResponseDto.BaseResultDto.builder()
//                .memberId(member.getId())
//                .question(memberHealQuestion.getQuestion())
//                .savedAnswers(memberHealQuestion.getAnswers())
//                .build();
//
//        return HealInfoResponseDto.ChangeBaseResultDto.builder()
//                .baseResultDto(result)
//                .healEatFoods(member.getHealEatFoods())
//                .build();
//    }
}
