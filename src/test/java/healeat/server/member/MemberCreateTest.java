package healeat.server.member;

import healeat.server.domain.Member;
import healeat.server.domain.enums.DietAns;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Vegeterian;
import healeat.server.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

// static import 편리
import static healeat.server.domain.enums.Answer.*;

@SpringBootTest
@Transactional
public class MemberCreateTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void createMemberTest() throws Exception {

        Member member = Member.builder()
                .name("배성우")
                .dietAnswer(DietAns.NONE)
                .vegetAnswer(Vegeterian.NONE)
                .build();
        memberRepository.save(member);
        System.out.println("멤버의 건강 정보 목록: "+ member.getHealthInfoMap().toString());
    }

    @Test
    public void useMemberHealthInfo() throws Exception {

        Member member = Member.builder()
                .name("배성우")
                .dietAnswer(DietAns.NONE)
                .vegetAnswer(Vegeterian.NONE)
                .build();
        memberRepository.save(member);

        /**
         * 답변 로직 (피해야 하는 음식으로 예를 들겠음)
         */
        // userInput Set이 request로 넘어옴
        Set<Answer> userAnsSet = EnumSet.noneOf(Answer.class);
        userAnsSet.add(ALCOHOL);    userAnsSet.add(DAIRY);
        userAnsSet.add(CAFFEINE);   userAnsSet.add(MEAT);
        //enums.HealthInfo.*; 스태틱 임포트 필수

        // 멤버 필드에 추가
        Map<Question, Set<Answer>> healthInfoMap = member.getHealthInfoMap();
        healthInfoMap.put(Question.FOOD_TO_AVOID, userAnsSet);

        System.out.println("멤버의 건강 정보 목록: " + member.getHealthInfoMap().toString());
    }
}