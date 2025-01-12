package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.domain.Question;
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
    private final QuestionRepository questionRepository;
    private final MemberMealNeededRepository memberMealNeededRepository;
    private final MemberNutrientNeededRepository memberNutrientNeededRepository;
    private final MemberFoodToAvoidRepository memberFoodToAvoidRepository;

    @Transactional(readOnly = true)
    public MemberHealthInfoResponseDto getMemberHealthInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // mock 데이터
        return MemberHealthInfoResponseDto.builder()
                //
                .build();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // Mock 데이터
        return QuestionResponseDto.builder()
                //
                .build();
    }

    @Transactional
    public AnswerResponseDto saveAnswer(Long questionId, AnswerRequestDto request) {

        return AnswerResponseDto.builder()
                .questionId(questionId)
                .memberId(Long.valueOf(request.getMemberId()))
                .selectedOptions(request.getSelectedOptions())
                .build();
    }

    @Transactional
    public AnswerResponseDto updateAnswer(Long memberId, Long questionId, AnswerRequestDto request) {

        return AnswerResponseDto.builder()
                .questionId(questionId)
                .memberId(memberId)
                .selectedOptions(request.getSelectedOptions())
                .build();
    }
}
