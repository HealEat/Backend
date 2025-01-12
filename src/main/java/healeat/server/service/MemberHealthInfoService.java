package healeat.server.service;

import healeat.server.domain.HealthInfoAnswer;
import healeat.server.domain.Member;
import healeat.server.domain.HealthInfoQuestion;
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
    private final HealthInfoQuestionRepository questionRepository;
    private final HealthInfoAnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public MemberHealthInfoResponseDto getMemberHealthInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 회원의 관련 데이터 조회
        List<QuestionResponseDto> questions = member.getHealthInfoQuestions().stream()
                .map(question -> QuestionResponseDto.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestion().name())
                        .answers(question.getAnswers().stream()
                                .map(answer -> answer.getAnswer().name())
                                .toList())
                        .build())
                .toList();

        return MemberHealthInfoResponseDto.builder()
                .memberId(memberId)
                .questions(questions)
                .build();
    }

    @Transactional
    public QuestionResponseDto getQuestion(Long questionId) {
        HealthInfoQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // Mock 데이터
        return QuestionResponseDto.builder()
                //
                .build();
    }

    @Transactional
    public AnswerResponseDto saveAnswer(Long questionId, AnswerRequestDto request) {

        HealthInfoQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        List<HealthInfoAnswer> answers = request.getSelectedAnswers().stream()
                .map(answer -> HealthInfoAnswer.builder()
                        .healthInfoQuestion(question)
                        .answer(answer)
                        .build())
                .toList();

        answerRepository.saveAll(answers);

        return AnswerResponseDto.builder()
                .questionId(questionId)
                .memberId(Long.valueOf(request.getMemberId()))
                .selectedOptions(request.getSelectedAnswers().stream()
                        .map(Enum::name)
                        .toList())
                .build();
    }

    @Transactional
    public AnswerResponseDto updateAnswer(Long memberId, Long questionId, AnswerRequestDto request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        HealthInfoQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // 기존 답변 삭제
        answerRepository.deleteByHealthInfoQuestion(question);

        // 새로운 답변 저장
        List<HealthInfoAnswer> answers = request.getSelectedAnswers().stream()
                .map(answer -> HealthInfoAnswer.builder()
                        .healthInfoQuestion(question)
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
