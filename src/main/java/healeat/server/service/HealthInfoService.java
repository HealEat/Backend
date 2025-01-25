package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthInfoHandler;
import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Question;
import healeat.server.repository.*;
import healeat.server.web.dto.AnswerRequestDto;
import healeat.server.web.dto.AnswerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthInfoService {

    private final MemberHealQuestionRepository memberHealQuestionRepository;

    // Question에 대한 회원의 답변 저장
    @Transactional
    public AnswerResponseDto createQuestion(Member member, Integer questionNum, AnswerRequestDto request) {

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

        memberHealQuestionRepository.save(memberHealQuestion);

        return AnswerResponseDto.builder()
                .memberId(member.getId())
                .question(question)
                .selectedAnswers(answers)
                .build();
    }

    // Question에 대한 회원의 답변 수정
    @Transactional
    public AnswerResponseDto updateQuestion(Member member, Integer questionNum, AnswerRequestDto request) {

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

        memberHealQuestion.updateAnswers(answers);

        return AnswerResponseDto.builder()
                .memberId(member.getId())
                .question(question)
                .selectedAnswers(answers)
                .build();
    }
}
