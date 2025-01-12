package healeat.server.service;

import healeat.server.domain.HealthPlan;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.web.dto.HealthPlanUpdateRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthPlanService {

    private final HealthPlanRepository healthPlanRepository;

    public HealthPlanService(HealthPlanRepository healthPlanRepository) {
        this.healthPlanRepository = healthPlanRepository;
    }

    public List<HealthPlan> getAllHealthPlans() {
        return healthPlanRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public HealthPlan getHealthPlanById(Long id) {
        return healthPlanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("HealthPlan not found with id: " + id));
    }

    public HealthPlan createHealthPlan(HealthPlan healthPlan) {
        return healthPlanRepository.save(healthPlan);
    }

    public HealthPlan updateHealthPlanPartial(Long id, HealthPlanUpdateRequestDto updateRequest) {
        HealthPlan existingHealthPlan = getHealthPlanById(id);
        return existingHealthPlan.updateHealthPlan(
                updateRequest.getDuration(),
                updateRequest.getNumber(),
                updateRequest.getGoal()
        );
    }

    public void deleteHealthPlan(Long id) {
        healthPlanRepository.deleteById(id);
    }
}
