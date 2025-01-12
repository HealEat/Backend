package healeat.server.member;

import healeat.server.domain.Member;
import healeat.server.domain.enums.DietAns;
import healeat.server.domain.enums.Vegeterian;
import healeat.server.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
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
        System.out.println("멤버의 건강 정보 목록: "+ member.getHealthInfoByCategories().toString());
    }
}
