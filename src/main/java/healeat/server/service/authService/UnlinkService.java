package healeat.server.service.authService;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.repository.MemberRepository;
import healeat.server.repository.ReviewRepository.ReviewRepository;
import healeat.server.service.HealthPlanService;
import healeat.server.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import healeat.server.repository.MemberTermRepository;

import java.util.List;
import java.util.Optional;

import static healeat.server.domain.Member.ANONYMOUS_MEMBER_ID;

@Service
@RequiredArgsConstructor
public class UnlinkService {

    private final MemberRepository memberRepository;
    private final HealthPlanRepository healthPlanRepository;
    private final HealthPlanService healthPlanService;
    private final ImageService imageService;
    private final ReviewRepository reviewRepository;
    private final MemberTermRepository memberTermRepository;

    @Transactional
    public void deleteSocialMember(String provider, String providerId) {

        // 회원의 모든 건강관리목표 삭제 (for images)
        Optional<Member> member = memberRepository.findByProviderAndProviderId(provider, providerId);

        List<HealthPlan> healthPlans = healthPlanRepository.findByMember(member.get());
        if (!healthPlans.isEmpty()) {
            for(HealthPlan healthPlan : healthPlans) {
                healthPlanService.deleteHealthPlan(healthPlan.getId());
            }
        }

        // 프로필 삭제
        imageService.deleteProfileImage(member.get().getId());

        // 회원의 모든 리뷰 데이터 익명화
        List<Review> reviews = reviewRepository.findByMember(member.get());
        reviews.forEach(this::setReviewToAnonymous);

        //  회원의 약관 동의 기록 삭제
        memberTermRepository.deleteByMember(member.get());

        System.out.println(" DB에서 삭제: provider=" + provider + ", providerId=" + providerId);
        memberRepository.deleteByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public void setReviewToAnonymous(Review review) {
        Member anonymousMember = memberRepository.findById(ANONYMOUS_MEMBER_ID)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.ANONYMOUS_NOT_FOUND));

        review.setMember(anonymousMember);
    }
}
