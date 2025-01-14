package healeat.server.service;

import healeat.server.domain.HealthInfoAnswer;
import healeat.server.domain.Member;
import healeat.server.domain.MemberHealQuestion;
import healeat.server.repository.*;
import healeat.server.web.dto.AnswerRequestDto;
import healeat.server.web.dto.AnswerResponseDto;
import healeat.server.web.dto.MemberHealthInfoResponseDto;
import healeat.server.web.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberHealthInfoService {

    private final MemberRepository memberRepository;
    private final MemberHealQuestionRepository questionRepository;
    private final HealthInfoAnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public MemberHealthInfoResponseDto getMemberHealthInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 회원의 건강 정보 질문 및 답변 데이터 전체 조회
        List<QuestionResponseDto> questions = member.getMemberHealQuestions().stream()
                .map(question -> QuestionResponseDto.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestion().name())
                        .answers(question.getHealthInfoAnswers().stream()
                                .map(answer -> answer.getAnswer().name())
                                .toList())
                        .build())
                .toList();

        return MemberHealthInfoResponseDto.builder()
                .memberId(memberId)
                .questions(questions)
                .build();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(Long questionId) {
        MemberHealQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // 특정 질문 조회하기
        return QuestionResponseDto.builder()
                .questionId(question.getId())
                .questionText(question.getQuestion().name())
                .answers(question.getHealthInfoAnswers().stream()
                        .map(answer -> answer.getAnswer().name())
                        .toList())
                .build();
    }

    // 특정 질문에 대한 회원의 답변 저장
    @Transactional
    public AnswerResponseDto saveAnswer(Long memberId, Long questionId, AnswerRequestDto request) {

        MemberHealQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // 기존 답변 삭제
        answerRepository.deleteByMemberHealQuestion(question);

        // 새로운 답변 저장
        List<HealthInfoAnswer> answers = request.getSelectedAnswers().stream()
                .map(answer -> HealthInfoAnswer.builder()
                        .memberHealQuestion(question)
                        .answer(answer)
                        .build())
                .toList();

        answerRepository.saveAll(answers);

        return AnswerResponseDto.builder()
                .questionId(questionId)
                .memberId(memberId)
                .selectedOptions(request.getSelectedAnswers().stream()
                        .map(Enum::name)
                        .toList())
                .build();
    }

}
