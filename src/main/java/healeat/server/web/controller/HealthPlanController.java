package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
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
import org.springframework.http.MediaType;
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

    /**
     * 건강 관리 목표
     * C-R-D
     */
    @Operation(summary = "건강 관리 목표 추가", description = "건강 관리 목표를 추가합니다." +
            "(이미지와 메모는 아직 추가되지 않았음)")
    @PostMapping
    public ApiResponse<HealthPlanResponseDto.SetResultDto> createHealthPlan(
            @RequestBody HealthPlanRequestDto.HealthPlanUpdateRequestDto request,
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(HealthPlanConverter.toSetResultDto(
                healthPlanService.createHealthPlan(request, testMember)));
    }

    @Operation(summary = "건강 관리 목표 조회", description = "사용자의 건강 관리 목표를 전체 조회합니다.")
    @GetMapping
    public ApiResponse<HealthPlanResponseDto> getAllHealthPlans(
            @AuthenticationPrincipal Member member,
            @CheckPage @RequestParam Integer page) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanResponseDto(
                healthPlanService.find10PlansByMemberPage(testMember, page)));
    }

    @Operation(summary = "건강 관리 목표 삭제", description = "건강 관리 목표를 삭제합니다.")
    @DeleteMapping("/{planId}")
    public ApiResponse<HealthPlanResponseDto.DeleteResultDto> deleteHealthPlan(
            @PathVariable Long planId) {

        return ApiResponse.onSuccess(HealthPlanConverter.toDeleteResultDto(
                healthPlanService.deleteHealthPlan(planId)));
    }

    /**
     * 건강 관리 목표
     * UPDATE
     */
    @Operation(summary = "건강 관리 목표 Memo 작성", description = "건강 관리 목표의 메모를 저장합니다.")
    @PatchMapping("/{planId}/memo")
    public ApiResponse<HealthPlanResponseDto.MemoResponseDto> uploadMemo(
            @PathVariable Long planId,
            @RequestParam String memo) {

        return ApiResponse.onSuccess(HealthPlanConverter.toMemoResponseDto(
                healthPlanService.updateHealthPlanMemo(planId, memo)));
    }

    @Operation(summary = "건강 관리 목표 수정 - 세부사항과 이미지",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")),
            description =
                    """
                        건강 관리 목표 세부사항 업데이트와 이미지 업로드가 이루어집니다. 이미지는 10개까지 업로드할 수 있습니다.
                        삭제할 이미지 id들과 새롭게 업로드할 이미지 파일들을 주세요.
                        duration: DAY, WEEK, DAY10, MONTH""")
    @PostMapping(value = "/{planId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HealthPlanResponseDto.HealthPlanOneDto> updateHealthPlan(
            @PathVariable Long planId,
            @RequestPart HealthPlanRequestDto.HealthPlanUpdateRequestDto updateRequest,
            @RequestPart(name = "files", required = false)
            List<MultipartFile> uploadImages) {

        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanOneDto(
                healthPlanService.updateHealthPlan(planId, updateRequest, uploadImages)));
    }

    @Operation(summary = "건강관리목표 수정 - 상태", description = "선택한 건강관리목표 상태를 수정합니다. " +
            "status: FAIL, PROGRESS, COMPLETE")
    @PatchMapping("/{planId}/status")
    public ApiResponse<HealthPlanResponseDto.StatusResponseDto> updateHealthPlanStatus(
            @PathVariable Long planId,
            @RequestBody HealthPlanRequestDto.HealthPlanStatusUpdateRequestDto request) {

        return ApiResponse.onSuccess(HealthPlanConverter.toStatusResponseDto(
                healthPlanService.updateHealthPlanStatus(planId, request)));
    }

//    /***************************** 건강관리목표 이미지 *****************************/
//
//    @Operation(summary = "건강관리목표 이미지 등록", description = "건강 관리 목표 이미지를 추가합니다. (S3 포함)",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
//    @PostMapping(value = "/{planId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ApiResponse<HealthPlanResponseDto.HealthPlanImageResponseDto> createHealthPlanImage(
//            @PathVariable Long planId,
//            @RequestPart(name = "file")
//            MultipartFile file) {
//
//        HealthPlanImage createHealthPlanImage = healthPlanService.createHealthPlanImage(planId, file);
//        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageResponseDto(createHealthPlanImage));
//    }
//
//    @Operation(summary = "건강관리목표 이미지 URL 리스트 조회", description = "건강관리목표 이미지들의 URL 을 조회할 수 있습니다.")
//    @GetMapping("/{planId}/image-urls")
//    public ApiResponse<List<HealthPlanResponseDto.HealthPlanImageResponseDto>> getHealthPlanImages(
//            @PathVariable Long planId
//    ) {
//
//        List<HealthPlanImage> healthPlanImages = healthPlanService.getHealthPlanImages(planId);
//        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageListResponseDto(healthPlanImages));
//    }
//
//    @Operation(summary = "건강관리목표 이미지 조회", description = "건강관리목표 이미지를 반환합니다. (여러개 한 번에 반환이 안되네요.)")
//    @GetMapping("/image/{imageId}")
//    public ResponseEntity<Resource> viewHealthPlanImage(
//            @PathVariable Long imageId
//    ) {
//        return healthPlanService.getHealthPlanImageFiles(imageId);
//    }
//
//    @Operation(summary = "건강관리목표 이미지 삭제", description = "선택한 건강관리목표 이미지를 삭제합니다. (S3 포함)")
//    @DeleteMapping("/image/{imageId}")
//    public ApiResponse<HealthPlanResponseDto.HealthPlanImageResponseDto> deleteHealthPlanImage(
//            @PathVariable Long imageId) {
//        HealthPlanImage image = healthPlanService.deleteHealthPlanImage(imageId);
//        return ApiResponse.onSuccess(HealthPlanConverter.toHealthPlanImageResponseDto(image));
//    }
}