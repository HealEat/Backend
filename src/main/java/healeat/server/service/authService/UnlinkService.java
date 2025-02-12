package healeat.server.service.authService;

import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.repository.MemberRepository;
import healeat.server.service.HealthPlanService;
import healeat.server.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnlinkService {

    private final MemberRepository memberRepository;
    private final HealthPlanRepository healthPlanRepository;
    private final HealthPlanService healthPlanService;
    private final ImageService imageService;

    @Transactional
    public void deleteKakaoMember(String provider, String providerId) {

        /*********************** 멤버 관련 데이터 삭제 (건강관리목표 / 프로필) ***********************/
        // 건강관리목표 삭제
        Optional<Member> member = memberRepository.findByProviderAndProviderId(provider, providerId);

        List<HealthPlan> healthPlans = healthPlanRepository.findByMember(member.get());
        if (!healthPlans.isEmpty()) {
            for(HealthPlan healthPlan : healthPlans) {
                healthPlanService.deleteHealthPlan(healthPlan.getId());
            }
        }

        // 프로필 삭제
        imageService.deleteProfileImage(member.get().getId());

        System.out.println(" DB에서 삭제: provider=" + provider + ", providerId=" + providerId);
        memberRepository.deleteByProviderAndProviderId(provider, providerId);
    }
}
