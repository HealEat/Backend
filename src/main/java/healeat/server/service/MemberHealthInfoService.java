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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberHealthInfoService {

    private final MemberHealQuestionRepository memberHealQuestionRepository;
    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;

    @Transactional
    public Member chooseVegetarian(Member member, String choose) {

        Vegetarian vegetarian = Vegetarian.getByDescription(choose);
        member.setVegetAndCheckChanged(vegetarian);

        return member;
    }

    @Transactional
    public Member chooseDiet(Member member, String choose) {

        Diet diet = Diet.getByDescription(choose);
        member.setDietAndCheckChanged(diet);

        return member;
    }

    // Question에 대한 회원의 답변 저장
    @Transactional
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

    @Transactional
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
    public List<Disease> searchDiseases(String keyword) {
        return diseaseRepository.findByNameContaining(keyword);
    }

    /// MyPage - 나의 건강 정보 조회 API
    public HealInfoResponseDto.MyHealthInfoDto getMyHealthInfo(Member member) {

        // 1. 나의 건강 목표 (질병 관리, 비건, 다이어트)
        List<String> healthGoals = determineHealthGoals(member);

        // 2. 비건 종류
        String vegetarianType = member.getVegetarian() != null ?
                member.getVegetarian().getDescription() :
                "";

        // 3. 회원의 건강 질문 응답 조회
        Map<Question, List<Answer>> questionAnswers = getMemberHealthAnswer(member);
        List<String> healthIssues = getAnswerDescription(questionAnswers, Question.HEALTH_ISSUE);
        List<String> requiredMeals = getAnswerDescription(questionAnswers, Question.MEAL_NEEDED);
        List<String> requiredNutrients = getAnswerDescription(questionAnswers, Question.NUTRIENT_NEEDED);
        List<String> avoidFoods = getAnswerDescription(questionAnswers, Question.FOOD_TO_AVOID);

        // DTO 생성, 반환
        return HealInfoResponseDto.MyHealthInfoDto.builder()
                .healthGoals(healthGoals)
                .vegetarianType(vegetarianType)
                .healthIssues(healthIssues)
                .requiredMeals(requiredMeals)
                .requiredNutrients(requiredNutrients)
                .avoidedFoods(avoidFoods)
                .build();
    }

    private List<String> determineHealthGoals(Member member) {
        List<String> goals = new ArrayList<>();

        if (!memberDiseaseRepository.findByMember(member).isEmpty()) {
            goals.add("질병 관리");
        }
        if (member.getVegetarian() != Vegetarian.NONE) {
            goals.add("베지테리언");
        }
        if (member.getDiet() != Diet.NONE) {
            goals.add("다이어트");
        }

        return goals;
    }

    // 질문에 대한 회원의 응답 가져오기 메서드
    private Map<Question, List<Answer>> getMemberHealthAnswer(Member member) {
        return memberHealQuestionRepository.findByMember(member).stream()
                .collect(Collectors.toMap(
                        MemberHealQuestion::getQuestion,    // key: Question
                        q -> new ArrayList<>(q.getAnswers()), // 기존 리스트를 새로 복사
                        (existing, newValue) -> {
                            List<Answer> mergedList = new ArrayList<>(existing);
                            mergedList.addAll(newValue);
                            return mergedList;
                        }
                ));
    }

    // 특정 질문에 대한 답변 리스트 반환하는 메서드
    private List<String> getAnswerDescription(Map<Question, List<Answer>> questionAnswers, Question question) {
        return questionAnswers.getOrDefault(question, List.of()).stream()
                .map(Answer::getDescription)
                .distinct() // 중복 제거
                .toList();
    }

//    public Member updateVegetarian(Member member, String choose) {
//
//        Vegetarian vegetarian = Vegetarian.getByDescription(choose);
//
//        // 변경 사항 있을 시 알고리즘 새로 계산
//        boolean isChanged = member.setVegetAndCheckChanged(vegetarian);
//        if (isChanged) {
//            makeHealEat(member);
//        }
//
//        return member;
//    }
//    public Member updateDiet(Member member, String choose) {
//
//        Diet diet = Diet.getByDescription(choose);
//        member.setDietAndCheckChanged(diet);
//
//        // 변경 사항 있을 시 알고리즘 새로 계산
//        boolean isChanged = member.setDietAndCheckChanged(diet);
//        if (isChanged) {
//            makeHealEat(member);
//        }
//
//        return member;
//    }
//
//    // Question에 대한 회원의 답변 수정
//    public MemberHealQuestion updateQuestion(Member member, Integer questionNum, AnswerRequestDto request) {
//
//        // 1. HEALTH_ISSUE, 2. MEAL_NEEDED, 3. NUTRIENT_NEEDED, 4. FOOD_TO_AVOID
//        questionNum = Math.max(0, questionNum - 1);
//        Question question = Question.values()[questionNum];
//
//        List<Answer> answers = request.getSelectedAnswers().stream()
//                .map(Answer::getByDescription)
//                .toList();
//
//        List<MemberHealQuestion> memberHealQuestions = memberHealQuestionRepository.findByMember(member);
//        MemberHealQuestion memberHealQuestion = memberHealQuestions.stream()
//                .filter(mhq -> mhq.getQuestion() == question)
//                .findFirst().orElseThrow(() ->
//                        new HealthInfoHandler(ErrorStatus.QUESTION_NOT_FOUND));
//
//        // 변경 사항 있을 시 알고리즘 새로 계산
//        boolean isChanged = !(new HashSet<>(memberHealQuestion.getAnswers()).equals(new HashSet<>(answers)));
//        if (isChanged) {
//            memberHealQuestion.setAnswers(answers);
//            makeHealEat(member);
//        }
//
//        return memberHealQuestion;
//    }
}
