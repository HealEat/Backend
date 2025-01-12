package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.HealthPlan;
import healeat.server.service.HealthPlanService;
import healeat.server.converter.HealthPlanConverter;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.HealthPlanRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plan")
public class HealthPlanController {

    private final HealthPlanService healthPlanService;
    private final HealthPlanConverter healthPlanConverter;

    public HealthPlanController(HealthPlanService healthPlanService, HealthPlanConverter healthPlanConverter) {
        this.healthPlanService = healthPlanService;
        this.healthPlanConverter = healthPlanConverter;
    }

    // GET /plan
    @GetMapping
    public ResponseEntity<HealthPlanResponseDto.HealthPlanListDto> getAllHealthPlans() {
        List<HealthPlan> healthPlans = healthPlanService.getAllHealthPlans();
        List<HealthPlanResponseDto.HealthPlanOneDto> healthPlanDtos = healthPlans.stream()
                .map(healthPlanConverter::toHealthPlanOneDto)
                .collect(Collectors.toList());
        HealthPlanResponseDto.HealthPlanListDto response = HealthPlanResponseDto.HealthPlanListDto.builder()
                .HealthPlanList(healthPlanDtos)
                .build();
        return ResponseEntity.ok(response);
    }

    // POST /plan
    @Operation(summary = "건강 관리 목표 추가", description = "건강 관리 목표를 추가합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PostMapping
    public ApiResponse<HealthPlanResponseDto.setReusltDto> createHealthPlan(
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto request) {
        HealthPlan createdHealthPlan = healthPlanService.createHealthPlan(request);

        return ApiResponse.onSuccess(healthPlanConverter.toSetResultDto(createdHealthPlan));
    }

    // PATCH /plan/{planId}
    @PatchMapping("/{planId}")
    public ResponseEntity<HealthPlanResponseDto.HealthPlanOneDto> updateHealthPlanPartial(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto updateRequest) {
        HealthPlan updatedHealthPlan = healthPlanService.updateHealthPlanPartial(planId, updateRequest);
        HealthPlanResponseDto.HealthPlanOneDto response = healthPlanConverter.toHealthPlanOneDto(updatedHealthPlan);
        return ResponseEntity.ok(response);
    }

    // DELETE /plan/{planId}
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteHealthPlan(@PathVariable Long planId) {
        healthPlanService.deleteHealthPlan(planId);
        return ResponseEntity.noContent().build();
    }
}

