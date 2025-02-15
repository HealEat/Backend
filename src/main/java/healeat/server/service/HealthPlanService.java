package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.aws.s3.AmazonS3Manager;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.domain.Member;
import healeat.server.domain.enums.Duration;
import healeat.server.repository.HealthPlanImageRepository;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.web.dto.HealthPlanRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class HealthPlanService {

    private final HealthPlanRepository healthPlanRepository;
    private final HealthPlanImageRepository healthPlanImageRepository;
    private final AmazonS3Manager amazonS3Manager;

    /***************************** 건강관리목표를 위한 메서드 *****************************/

    @Transactional(readOnly = true)
    public HealthPlan getHealthPlanById(Long id) {
        return healthPlanRepository.findById(id).orElseThrow(() ->
                new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
    }

    // Page 로 나누어서 HealthPlan 조회
    @Transactional(readOnly = true)
    public Page<HealthPlan> find10PlansByMemberPage(Member member, Integer page) {

        int safePage = Math.max(0, page - 1);

        Pageable pageable = PageRequest.of(safePage, 10);

        return healthPlanRepository.findAllByMemberOrderByCreatedAtDesc(member, pageable);
    }

    public HealthPlan createHealthPlan(HealthPlanRequestDto.HealthPlanUpdateRequestDto request,
                                       Member member) {

        if(request.getNumber() < 1 || request.getNumber() > 10) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_GOAL_NUMBER);
        }

        HealthPlan healthPlan = HealthPlan.builder()
                .member(member)
                .duration(Duration.valueOf(request.getDuration()))
                .goalNumber(request.getNumber())
                .goal(request.getGoal())
                .build();

        return healthPlanRepository.save(healthPlan);
    }

    public HealthPlan updateHealthPlan(Long id,
                                       HealthPlanRequestDto.HealthPlanUpdateRequestDto request,
                                       List<MultipartFile> uploadImages) {
        HealthPlan healthPlan = getHealthPlanById(id);

        if (request.getNumber() < 1 || request.getNumber() > 10) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_GOAL_NUMBER);
        }

        healthPlan.updateHealthPlan(
                Duration.valueOf(request.getDuration()),
                request.getNumber(),
                request.getGoal()
        );

        return uploadImagesForPlan(
                request.getRemoveImageIds(), uploadImages, healthPlan);

    }

    public HealthPlan uploadImagesForPlan(List<Long> removeImageIds,
                                          List<MultipartFile> uploadImages,
                                          HealthPlan healthPlan) {

        if (removeImageIds != null && !removeImageIds.isEmpty()) {
            removeImageIds.forEach(healthPlanImageRepository::deleteById);
        }

        if (!(uploadImages == null || uploadImages.isEmpty())) {
            if (uploadImages.size() > 10) {
                throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_TOO_MANY_IMAGES);
            }
            for (MultipartFile file : uploadImages) {
                String keyName = amazonS3Manager.generateHealthPlanKeyName();
                String uploadFileUrl = amazonS3Manager.uploadFile(keyName, file);

                HealthPlanImage healthPlanImage = HealthPlanImage.builder()
                        .healthPlan(healthPlan)
                        .imageUrl(uploadFileUrl)
                        .build();

                healthPlan.addImage(healthPlanImage);
                healthPlanImageRepository.save(healthPlanImage);
            }
        }
        return healthPlan;
    }

    public HealthPlan deleteHealthPlan(Long planId) {
        HealthPlan healthPlan = healthPlanRepository.findById(planId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));

        // S3 저장 이미지 삭제
        List<HealthPlanImage> healthPlanImages = healthPlan.getHealthPlanImages();
        for(HealthPlanImage healthPlanImage : healthPlanImages) {
            amazonS3Manager.deleteFile(healthPlanImage.getImageUrl());
            healthPlanImageRepository.delete(healthPlanImage);
        }
        // 리뷰 삭제
        healthPlanRepository.delete(healthPlan);
        return healthPlan;
    }

    /***************************** 건강관리목표 메모를 위한 메서드 *****************************/

    public HealthPlan updateHealthPlanMemo(Long id, String memo) {

        HealthPlan existingHealthPlan = getHealthPlanById(id);
        return existingHealthPlan.updateMemo(memo);
    }

    /***************************** 건강관리목표 상태를 위한 메서드 *****************************/

    public HealthPlan updateHealthPlanStatus(Long planId,
                                             HealthPlanRequestDto.HealthPlanStatusUpdateRequestDto updateRequest) {
        HealthPlan healthPlan = healthPlanRepository.findById(planId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));

        return healthPlan.updateStatus(updateRequest.getStatus());
    }

//
//    /***************************** 건강관리목표 이미지를 위한 메서드 *****************************/
//
//    @Transactional
//    public HealthPlanImage createHealthPlanImage(Long planId, MultipartFile file) {
//
//        HealthPlan healthPlan = healthPlanRepository.findById(planId)
//                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
//
//        if(healthPlan.getHealthPlanImages().size() + 1 > 5) {
//            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_TOO_MANY_IMAGES);
//        }
//
//        String keyName = amazonS3Manager.generateHealthPlanKeyName();
//        String uploadFileUrl = amazonS3Manager.uploadFile(keyName, file);
//
//        HealthPlanImage healthPlanImage = HealthPlanImage.builder()
//                .healthPlan(healthPlan)
//                .imageUrl(uploadFileUrl)
//                .build();
//
//        return healthPlanImageRepository.save(healthPlanImage);
//    }
//
//    public List<HealthPlanImage> getHealthPlanImages(Long planId) {
//        HealthPlan healthPlan = healthPlanRepository.findById(planId)
//                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
//        return healthPlan.getHealthPlanImages();
//    }
//
//    @Transactional
//    public ResponseEntity<Resource> getHealthPlanImageFiles(Long imageId) {
//        HealthPlanImage healthPlanImage = healthPlanImageRepository.findById(imageId)
//                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_IMAGE_NOT_FOUND));
//
//        String healthPlanImageUrl = healthPlanImage.getImageUrl();
//
//        try {
//            URL url = new URL(healthPlanImageUrl);
//            InputStream inputStream = url.openStream();
//            byte[] imageBytes = inputStream.readAllBytes();
//            ByteArrayResource resource = new ByteArrayResource(imageBytes);
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_JPEG) // 필요하면 다른 MIME 타입으로 변경 가능
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"health-plan-image.jpg\"")
//                    .body(resource);
//        } catch (Exception e) {
//            throw new RuntimeException("이미지를 불러오는 중 오류가 발생했습니다.", e);
//        }
//    }
//
//    @Transactional
//    public HealthPlanImage deleteHealthPlanImage(Long imageId) {
//        HealthPlanImage image = healthPlanImageRepository.findById(imageId)
//                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_IMAGE_NOT_FOUND));
//
//        amazonS3Manager.deleteFile(image.getImageUrl());
//        healthPlanImageRepository.delete(image);
//
//        return image;
//    }
}


