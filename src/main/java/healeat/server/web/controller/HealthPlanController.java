package healeat.server.web.controller;

import healeat.server.domain.HealthPlan;
import healeat.server.service.HealthPlanService;
import healeat.server.converter.HealthPlanConverter;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.HealthPlanUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plan")
public class HealthPlanController {

    private final HealthPlanService healthPlanService;

    public HealthPlanController(HealthPlanService healthPlanService) {
        this.healthPlanService = healthPlanService;
    }

    @GetMapping
    public ResponseEntity<List<HealthPlanResponseDto>> getAllHealthPlans() {
        List<HealthPlan> healthPlans = healthPlanService.getAllHealthPlans();
        List<HealthPlanResponseDto> response = healthPlans.stream()
                .map(HealthPlanConverter::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<HealthPlanResponseDto> getHealthPlanById(@PathVariable Long planId) {
        HealthPlan healthPlan = healthPlanService.getHealthPlanById(planId);
        return ResponseEntity.ok(HealthPlanConverter.toDto(healthPlan));
    }

    @PostMapping
    public ResponseEntity<HealthPlanResponseDto> createHealthPlan(@RequestBody HealthPlan healthPlan) {
        HealthPlan savedHealthPlan = healthPlanService.createHealthPlan(healthPlan);
        return ResponseEntity.ok(HealthPlanConverter.toDto(savedHealthPlan));
    }

    @PatchMapping("/{planId}")
    public ResponseEntity<HealthPlanResponseDto> updateHealthPlan(@PathVariable Long planId, @RequestBody HealthPlanUpdateRequestDto updateRequest) {
        HealthPlan updatedHealthPlan = healthPlanService.updateHealthPlanPartial(planId, updateRequest);
        return ResponseEntity.ok(HealthPlanConverter.toDto(updatedHealthPlan));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteHealthPlan(@PathVariable Long planId) {
        healthPlanService.deleteHealthPlan(planId);
        return ResponseEntity.noContent().build();
    }
}

