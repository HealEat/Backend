package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.domain.HealthPlan;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.web.dto.HealthPlanRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HealthPlanService {

    private final HealthPlanRepository healthPlanRepository;

    public List<HealthPlan> getAllHealthPlans() {
        return healthPlanRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public HealthPlan getHealthPlanById(Long id) {
        return healthPlanRepository.findById(id).orElseThrow(() ->
                new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
    }

    public List<HealthPlan> getHealthPlanByMemberId(Long memberId) {
        return healthPlanRepository.findByMemberId(memberId);
    }

    @Transactional
    public HealthPlan createHealthPlan(HealthPlanRequestDto.HealthPlanUpdateRequestDto request) {

        if(request.getNumber() < 1 || request.getNumber() > 10) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_GOAL_NUMBER);
        }
        HealthPlan healthPlan = HealthPlan.builder()
                .duration(request.getDuration())
                .goalNumber(request.getNumber())
                .goal(request.getGoal())
                .build();

        return healthPlanRepository.save(healthPlan);
    }

    @Transactional
    public HealthPlan updateHealthPlanPartial(Long id, HealthPlanRequestDto.HealthPlanUpdateRequestDto updateRequest) {
        HealthPlan existingHealthPlan = getHealthPlanById(id);
        return existingHealthPlan.updateHealthPlan(
                updateRequest.getDuration(),
                updateRequest.getNumber(),
                updateRequest.getGoal()
        );
    }

    @Transactional
    public void deleteHealthPlan(Long id) {
        healthPlanRepository.deleteById(id);
    }
}
