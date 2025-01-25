package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthInfoHandler;
import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Question;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.repository.*;
import healeat.server.web.dto.AnswerRequestDto;
import healeat.server.web.dto.HealInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberHealthInfoService {

    private final MemberHealQuestionRepository memberHealQuestionRepository;
    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;

    public Member chooseVegetarian(Member member, String choose) {

        Vegetarian vegetarian = Vegetarian.getByDescription(choose);
        member.setVegetAndCheckChanged(vegetarian);
        String chooseName = vegetarian.name();

        return member;
    }
    public Member updateVegetarian(Member member, String choose) {

        Vegetarian vegetarian = Vegetarian.getByDescription(choose);
        String chooseName = vegetarian.name();

        // 변경 사항 있을 시 알고리즘 새로 계산
        boolean isChanged = member.setVegetAndCheckChanged(vegetarian);
        if (isChanged) {
            makeHealEat(member);
        }

        return member;
    }

    public Member chooseDiet(Member member, String choose) {

        Diet diet = Diet.getByDescription(choose);
        member.setDietAndCheckChanged(diet);
        String chooseName = diet.name();

        return member;
    }
    public Member updateDiet(Member member, String choose) {

        Diet diet = Diet.getByDescription(choose);
        member.setDietAndCheckChanged(diet);
        String chooseName = diet.name();

        // 변경 사항 있을 시 알고리즘 새로 계산
        boolean isChanged = member.setDietAndCheckChanged(diet);
        if (isChanged) {
            makeHealEat(member);
        }

        return member;
    }

    // Question에 대한 회원의 답변 저장
    public MemberHealQuestion createQuestion(Member member, Integer questionNum, AnswerRequestDto request) {

        // 1. HEALTH_ISSUE, 2. MEAL_NEEDED, 3. NUTRIENT_NEEDED, 4. FOOD_TO_AVOID
        questionNum = Math.max(0, questionNum - 1);
        Question question = Question.values()[questionNum];

        List<Answer> answers = request.getSelectedAnswers().stream()
                .map(Answer::getByDescription)
                .toList();

        MemberHealQuestion memberHealQuestion = MemberHealQuestion.builder()
                .member(member)
                .question(question)
                .answers(answers)
                .build();

        return memberHealQuestionRepository.save(memberHealQuestion);
    }
    // Question에 대한 회원의 답변 수정
    public MemberHealQuestion updateQuestion(Member member, Integer questionNum, AnswerRequestDto request) {

        // 1. HEALTH_ISSUE, 2. MEAL_NEEDED, 3. NUTRIENT_NEEDED, 4. FOOD_TO_AVOID
        questionNum = Math.max(0, questionNum - 1);
        Question question = Question.values()[questionNum];

        List<Answer> answers = request.getSelectedAnswers().stream()
                .map(Answer::getByDescription)
                .toList();

        List<MemberHealQuestion> memberHealQuestions = memberHealQuestionRepository.findByMember(member);
        MemberHealQuestion memberHealQuestion = memberHealQuestions.stream()
                .filter(mhq -> mhq.getQuestion() == question)
                .findFirst().orElseThrow(() ->
                        new HealthInfoHandler(ErrorStatus.QUESTION_NOT_FOUND));

        // 변경 사항 있을 시 알고리즘 새로 계산
        boolean isChanged = !(new HashSet<>(memberHealQuestion.getAnswers()).equals(new HashSet<>(answers)));
        if (isChanged) {
            memberHealQuestion.setAnswers(answers);
            makeHealEat(member);
        }

        return memberHealQuestion;
    }

    public HealInfoResponseDto makeHealEat(Member member) {

        List<Answer> answers = new ArrayList<>();

        member.getMemberHealQuestions()
                .forEach(mhq -> answers.addAll(mhq.getAnswers()));

        List<String> healEatFoods = FoodRecommendation
                .getFinalFoods(answers, member.getVegetarian(), member.getDiet());

        member.setHealEatFoods(healEatFoods);

        return HealInfoResponseDto.builder()
                .memberId(member.getId())
                .healEatFoods(healEatFoods)
                .build();
    }

    // 질병 검색 기능
    @Transactional(readOnly = true)
    public List<Disease> searchDiseases(String keyword) {
        return diseaseRepository.findByNameContaining(keyword);
    }

    // 회원이 선택한 질병 저장 기능
    @Transactional
    public void saveMemberDiseases(Member member, List<Long> diseaseIds) {

        List<Disease> diseases = diseaseRepository.findAllById(diseaseIds);

        member.setDiseases(diseases);
        memberRepository.save(member);
    }

}
