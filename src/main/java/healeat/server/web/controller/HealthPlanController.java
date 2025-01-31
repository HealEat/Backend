package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.HealthPlanService;
import healeat.server.converter.HealthPlanConverter;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.HealthPlanRequestDto;
import healeat.server.web.dto.ImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class HealthPlanController {

    private final S3Uploader s3Uploader;
    private final HealthPlanService healthPlanService;
    private final HealthPlanConverter healthPlanConverter;
    private final MemberRepository memberRepository;


    /*
     GET/plan - 건강 관리 목표 전체 조회
     */
    @Operation(summary = "건강 관리 목표 조회", description = "사용자의 건강 관리 목표를 전체 조회합니다.")
    @GetMapping
    public ApiResponse<HealthPlanResponseDto.HealthPlanListDto> getAllHealthPlans(
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        //정상적으로 HealthPlan 조회
        List<HealthPlan> healthPlans = healthPlanService.getHealthPlanByMember(testMember);
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
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto request,
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        HealthPlan createdHealthPlan = healthPlanService.createHealthPlan(request, testMember);

        return ApiResponse.onSuccess(healthPlanConverter.toSetResultDto(createdHealthPlan));
    }

    /*
     PATCH/plan/{planId} - 건강 관리 목표 수정
     */
    @Operation(summary = "건강 관리 목표 수정", description = "건강 관리 목표를 수정합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)" + "Duration ENUM 값 : DAY, WEEK, DAY10, MONTH")
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
    public ApiResponse<HealthPlanResponseDto.deleteResultDto> deleteHealthPlan(
            @PathVariable Long planId) {

        HealthPlan deleteHealthPlan = healthPlanService.getHealthPlanById(planId);
        HealthPlanResponseDto.deleteResultDto response = healthPlanConverter.toDeleteResultDto(deleteHealthPlan);

        healthPlanService.deleteHealthPlan(planId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Presigned/Public URL 리스트 생성 및 HealthPlanImage 등록", description = "이미지를 업로드/조회하기 위한 Presigned/Public URL 을 생성합니다")
    @PostMapping("/{planId}/upload-images")
    public ApiResponse<List<ImageResponseDto.PresignedUrlDto>> uploadUrls(
            @PathVariable Long planId,
            @RequestBody List<HealthPlanRequestDto.HealthPlanImageRequestDto> requests
    ) {
        // Service 호출: Presigned URL 및 Public URL 생성
        return ApiResponse.onSuccess(healthPlanService.generateImageUrls(planId, requests));
    }

    @Operation(summary = "건강 관리 목표 Memo 등록", description = "건강 관리 목표의 메모 글을 등록합니다.")
    @PatchMapping("/{planId}/memo")
    public ApiResponse<HealthPlanResponseDto.MemoResponseDto> uploadUrls(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanMemoUpdateRequestDto request
    ) {
        // Service 호출: 메모 업데이트 메서드
        HealthPlan updateHealthPlan = healthPlanService.updateHealthPlanMemo(planId, request);
        return ApiResponse.onSuccess(healthPlanConverter.toMemoResponseDto(updateHealthPlan));
    }

}