package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.HealthPlan;
import healeat.server.service.HealthPlanService;
import healeat.server.converter.HealthPlanConverter;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.HealthPlanRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class HealthPlanController {

    private final HealthPlanService healthPlanService;
    private final HealthPlanConverter healthPlanConverter;


    /*
     GET/plan - 건강 관리 목표 전체 조회
     */
    @Operation(summary = "건강 관리 목표 조회", description = "건강 관리 목표를 전체 조회합니다.")
    @GetMapping
    public ApiResponse<HealthPlanResponseDto.HealthPlanListDto> getAllHealthPlans() {
        List<HealthPlan> healthPlans = healthPlanService.getAllHealthPlans();
        List<HealthPlanResponseDto.HealthPlanOneDto> healthPlanDtoList = healthPlans.stream()
                .map(healthPlanConverter::toHealthPlanOneDto)
                .collect(Collectors.toList());
        HealthPlanResponseDto.HealthPlanListDto response = HealthPlanResponseDto.HealthPlanListDto.builder()
                .HealthPlanList(healthPlanDtoList)
                .build();
        return ApiResponse.onSuccess(response);
    }

    /*
     POST/plan - 건강 관리 목표 등록
     */
    @Operation(summary = "건강 관리 목표 추가", description = "건강 관리 목표를 추가합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PostMapping
    public ApiResponse<HealthPlanResponseDto.setResultDto> createHealthPlan(
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto request) {
        HealthPlan createdHealthPlan = healthPlanService.createHealthPlan(request);

        return ApiResponse.onSuccess(healthPlanConverter.toSetResultDto(createdHealthPlan));
    }

    /*
     PATCH/plan/{planId} - 건강 관리 목표 수정
     */
    @Operation(summary = "건강 관리 목표 수정", description = "건강 관리 목표를 수정합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PatchMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.HealthPlanOneDto> updateHealthPlanPartial(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto updateRequest) {
        HealthPlan updatedHealthPlan = healthPlanService.updateHealthPlanPartial(planId, updateRequest);
        HealthPlanResponseDto.HealthPlanOneDto response = healthPlanConverter.toHealthPlanOneDto(updatedHealthPlan);
        return ApiResponse.onSuccess(response);
    }

    /*
     DELETE/plan/{planId} - 건강 관리 목표 삭제
     */
    @Operation(summary = "건강 관리 목표 삭제", description = "건강 관리 목표를 삭제합니다.")
    @DeleteMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.deleteResultDto> deleteHealthPlan(@PathVariable Long planId) {
        HealthPlan deleteHealthPlan = healthPlanService.getHealthPlanById(planId);
        HealthPlanResponseDto.deleteResultDto response = healthPlanConverter.toDeleteResultDto(deleteHealthPlan);

        healthPlanService.deleteHealthPlan(planId);
        return ApiResponse.onSuccess(response);
    }
}