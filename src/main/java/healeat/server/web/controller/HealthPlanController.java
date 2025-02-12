package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.HealthPlanService;
import healeat.server.converter.HealthPlanConverter;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.HealthPlanResponseDto;
import healeat.server.web.dto.HealthPlanRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
@Validated
public class HealthPlanController {

    private final HealthPlanService healthPlanService;
    private final MemberRepository memberRepository;


    /***************************** 건강관리목표 *****************************/

    @Operation(summary = "건강 관리 목표 조회", description = "사용자의 건강 관리 목표를 전체 조회합니다.")
    @GetMapping
    public ApiResponse<HealthPlanResponseDto> getAllHealthPlans(
            @AuthenticationPrincipal Member member,
            @CheckPage @RequestParam Integer page) {

        Member testMember = memberRepository.findById(999L).get();

        //정상적으로 HealthPlan 조회
        Page<HealthPlan> healthPlans = healthPlanService.find10PlansByMemberPage(testMember, page);

        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanResponseDto(healthPlans));
    }

    @Operation(summary = "건강 관리 목표 추가", description = "건강 관리 목표를 추가합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PostMapping
    public ApiResponse<HealthPlanResponseDto.SetResultDto> createHealthPlan(
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto request,
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        HealthPlan createdHealthPlan = healthPlanService.createHealthPlan(request, testMember);

        return ApiResponse.onSuccess(HealthPlanConverter.toSetResultDto(createdHealthPlan));
    }

    @Operation(summary = "건강 관리 목표 수정", description = "건강 관리 목표를 수정합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)" + "Duration ENUM 값 : DAY, WEEK, DAY10, MONTH")
    @PatchMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.HealthPlanOneDto> updateHealthPlanPartial(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto updateRequest) {

        HealthPlan updatedHealthPlan = healthPlanService.updateHealthPlanPartial(planId, updateRequest);
        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanOneDto(updatedHealthPlan));
    }

    @Operation(summary = "건강 관리 목표 삭제", description = "건강 관리 목표를 삭제합니다.")
    @DeleteMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.DeleteResultDto> deleteHealthPlan(
            @PathVariable Long planId) {

        HealthPlan deletedHealthPlan= healthPlanService.deleteHealthPlan(planId);
        return ApiResponse.onSuccess(HealthPlanConverter.toDeleteResultDto(deletedHealthPlan));
    }

    /***************************** 건강관리목표 메모 *****************************/

    @Operation(summary = "건강 관리 목표 Memo 등록", description = "건강 관리 목표의 메모 글을 등록합니다.")
    @PatchMapping("/{planId}/memo")
    public ApiResponse<HealthPlanResponseDto.MemoResponseDto> uploadMemo(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanMemoUpdateRequestDto request
    ) {
        // Service 호출: 메모 업데이트 메서드
        HealthPlan updateHealthPlan = healthPlanService.updateHealthPlanMemo(planId, request);
        return ApiResponse.onSuccess(HealthPlanConverter.toMemoResponseDto(updateHealthPlan));
    }

    /***************************** 건강관리목표 이미지 *****************************/

    @Operation(summary = "건강관리목표 이미지 등록", description = "건강 관리 목표 이미지를 추가합니다. (S3 포함)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/{planId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HealthPlanResponseDto.HealthPlanImageResponseDto> createHealthPlanImage(
            @PathVariable Long planId,
            @RequestPart(name = "file")
            MultipartFile file) {

        HealthPlanImage createHealthPlanImage = healthPlanService.createHealthPlanImage(planId, file);
        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageResponseDto(createHealthPlanImage));
    }

    @Operation(summary = "건강관리목표 이미지 URL 리스트 조회", description = "건강관리목표 이미지들의 URL 을 조회할 수 있습니다.")
    @GetMapping("/{planId}/image-urls")
    public ApiResponse<List<HealthPlanResponseDto.HealthPlanImageResponseDto>> getHealthPlanImages(
            @PathVariable Long planId
    ) {

        List<HealthPlanImage> healthPlanImages = healthPlanService.getHealthPlanImages(planId);
        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageListResponseDto(healthPlanImages));
    }

    @Operation(summary = "건강관리목표 이미지 조회", description = "건강관리목표 이미지를 반환합니다. (여러개 한 번에 반환이 안되네요..)")
    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> viewHealthPlanImage(
            @PathVariable Long imageId
    ) {
        return healthPlanService.getHealthPlanImageFiles(imageId);
    }

    @Operation(summary = "건강관리목표 이미지 삭제", description = "선택한 건강관리목표 이미지를 삭제합니다. (S3 포함)")
    @DeleteMapping("/image/{imageId}")
    public ApiResponse<HealthPlanResponseDto.HealthPlanImageResponseDto> deleteHealthPlanImage(
            @PathVariable Long imageId) {
        HealthPlanImage image = healthPlanService.deleteHealthPlanImage(imageId);
        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageResponseDto(image));
    }

    /***************************** 건강관리목표 상태 *****************************/

    @Operation(summary = "건강관리목표 상태 수정", description = "선택한 건강관리목표 상태를 수정합니다")
    @PatchMapping("/{planId}/status")
    public ApiResponse<HealthPlanResponseDto.StatusResponseDto> updateHealthPlanStatus(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanStatusUpdateRequestDto request) {

        HealthPlan healthPlan = healthPlanService.updateHealthPlanStatus(planId, request);
        return ApiResponse.onSuccess(HealthPlanConverter.toStatusResponseDto(healthPlan));
    }

}