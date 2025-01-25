package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
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


    /*
     GET/plan - 건강 관리 목표 전체 조회
     */
    @Operation(summary = "건강 관리 목표 조회", description = "사용자의 건강 관리 목표를 전체 조회합니다.")
    @GetMapping
    public ApiResponse<HealthPlanResponseDto.HealthPlanListDto> getAllHealthPlans(
            @AuthenticationPrincipal Member member) {

        //정상적으로 HealthPlan 조회
        List<HealthPlan> healthPlans = healthPlanService.getHealthPlanByMemberId(member.getId());
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
        HealthPlan createdHealthPlan = healthPlanService.createHealthPlan(request, member);

        return ApiResponse.onSuccess(healthPlanConverter.toSetResultDto(createdHealthPlan));
    }

    /*
     PATCH/plan/{planId} - 건강 관리 목표 수정
     */
    @Operation(summary = "건강 관리 목표 수정", description = "건강 관리 목표를 수정합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PatchMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.HealthPlanOneDto> updateHealthPlanPartial(
            @AuthenticationPrincipal Member member,
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
            @AuthenticationPrincipal Member member,
            @PathVariable Long planId) {
        HealthPlan deleteHealthPlan = healthPlanService.getHealthPlanById(planId);
        HealthPlanResponseDto.deleteResultDto response = healthPlanConverter.toDeleteResultDto(deleteHealthPlan);

        healthPlanService.deleteHealthPlan(planId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Presigned URL 리스트 생성", description = "이미지를 업로드하기 위한 Presigned URL 을 생성합니다")
    @GetMapping("/{planId}/upload-urls")
    public ApiResponse<List<ImageResponseDto.PresignedUrlDto>> uploadUrls(
            @AuthenticationPrincipal final Member member,
            @PathVariable Long planId,
            @RequestParam String imageType,
            @RequestParam int count,
            @RequestParam String imageExtension
    ) {
        List<ImageResponseDto.PresignedUrlDto> urls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ImageResponseDto.PresignedUrlDto urlPair = s3Uploader.createPresignedUrl(imageType, imageExtension);
            urls.add(urlPair);
        }
        return ApiResponse.onSuccess(urls);
    }

    @Operation(summary = "Public URL 리스트 조회", description = "이미지를 조회하기 위한 Public URL 을 조회합니다")
    @GetMapping("/public-urls")
    public ApiResponse<List<String>> readUrls(
            @AuthenticationPrincipal final Member member,
            @RequestParam String imageType,
            @RequestParam int count,
            @RequestParam String imageExtension
    ) {
        List<String> publicUrls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Public URL은 createPresignedUrl 메서드에서 생성된 값 사용 가능
            ImageResponseDto.PresignedUrlDto urlPair = s3Uploader.createPresignedUrl(imageType, imageExtension);
            publicUrls.add(urlPair.getPublicUrl());
        }
        return ApiResponse.onSuccess(publicUrls);
    }
}