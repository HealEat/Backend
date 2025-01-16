package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHealthInfoHandler;
import healeat.server.converter.MemberHealthInfoConverter;
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
    private final MemberHealthInfoConverter converter;

    // 회원의 건강 정보 질문 및 답변 데이터 전체 조회
    @Transactional(readOnly = true)
    public MemberHealthInfoResponseDto getMemberHealthInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHealthInfoHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<QuestionResponseDto> questions = member.getMemberHealQuestions().stream()
                .map(converter::toQuestionResponseDto)
                .toList();

        return converter.toMemberHealthInfoResponseDto(member, questions);
    }

    // 특정 질문 조회하기
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(Integer questionId) {
        MemberHealQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new MemberHealthInfoHandler(ErrorStatus.QUESTION_NOT_FOUND));

        return converter.toQuestionResponseDto(question);
    }

    // 특정 질문에 대한 회원의 답변 저장
    @Transactional
    public AnswerResponseDto saveAnswer(Long memberId, Integer questionId, AnswerRequestDto request) {

        MemberHealQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new MemberHealthInfoHandler(ErrorStatus.QUESTION_NOT_FOUND));

        // 기존 답변 삭제
        answerRepository.deleteByMemberHealQuestion(question);

        // 새로운 답변 저장
        List<HealthInfoAnswer> answers = converter.toHealthInfoAnswers(request.getSelectedAnswers(), question);
        answerRepository.saveAll(answers);

        return converter.toAnswerResponseDto(memberId, questionId, request.getSelectedAnswers());
    }

}
