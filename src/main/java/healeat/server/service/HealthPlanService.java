package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.aws.s3.AmazonS3Manager;
import healeat.server.aws.s3.S3PresignedUploader;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.domain.Member;
import healeat.server.repository.HealthPlanImageRepository;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.web.dto.HealthPlanRequestDto;
import healeat.server.web.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HealthPlanService {

    private final S3Uploader s3Uploader;
    private final S3PresignedUploader s3PresignedUploader;
    private final HealthPlanRepository healthPlanRepository;
    private final HealthPlanImageRepository healthPlanImageRepository;
    private final AmazonS3Manager amazonS3Manager;

    public List<HealthPlan> getAllHealthPlans() {
        return healthPlanRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public HealthPlan getHealthPlanById(Long id) {
        return healthPlanRepository.findById(id).orElseThrow(() ->
                new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
    }

    public List<HealthPlan> getHealthPlanByMember(Member member) {
        return healthPlanRepository.findByMember(member);
    }

    @Transactional
    public HealthPlan createHealthPlan(HealthPlanRequestDto.HealthPlanUpdateRequestDto request, Member member) {

        if(request.getNumber() < 1 || request.getNumber() > 10) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_GOAL_NUMBER);
        }
        HealthPlan healthPlan = HealthPlan.builder()
                .member(member)
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
    public void deleteHealthPlan(Long id) {healthPlanRepository.deleteById(id);}

    /*
     *   HealthPlan Memo Service
     */

    @Transactional
    public HealthPlan updateHealthPlanMemo(Long id, HealthPlanRequestDto.HealthPlanMemoUpdateRequestDto updateRequest) {
        HealthPlan existingHealthPlan = getHealthPlanById(id);
        return existingHealthPlan.updateMemo(
                updateRequest.getMemo()
        );
    }

    /*
     *   HealthPlan Image Service
     */

    public List<HealthPlanImage> getHealthPlanImageByPlanId(HealthPlan healthPlan) {
        return healthPlanImageRepository.findAllByHealthPlanId(healthPlan);
    }

    @Transactional
    public List<ImageResponseDto.PresignedUrlDto> uploadImagesToS3(
            Long planId, List<MultipartFile> files, List<HealthPlanRequestDto.HealthPlanImageRequestDto> requests
    ) throws Exception {
        HealthPlan healthPlan = healthPlanRepository.findById(planId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));

        if (healthPlan.getHealthPlanImages().size() + requests.size() > 5) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_TOO_MANY_IMAGES);
        }

        List<ImageResponseDto.PresignedUrlDto> presignedUrls = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            HealthPlanRequestDto.HealthPlanImageRequestDto request = requests.get(i);
            MultipartFile file = files.get(i);

            ImageResponseDto.PresignedUrlDto urlMap = s3Uploader.createPresignedUrl(request.getImageType(), request.getImageExtension());
            presignedUrls.add(urlMap);

            Path tempFilePath = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFilePath.toFile());
            s3PresignedUploader.uploadFileToS3(urlMap.getPresignedUrl(), tempFilePath);
            Files.delete(tempFilePath);

            HealthPlanImage healthPlanImage = HealthPlanImage.builder()
                    .healthPlan(healthPlan)
                    .imageUrl(urlMap.getPublicUrl())
                    .build();
            healthPlan.getHealthPlanImages().add(healthPlanImage);
        }

        healthPlanRepository.save(healthPlan);
        return presignedUrls;
    }

    public List<String> getHealthPlanImages(Long planId) {
        HealthPlan healthPlan = healthPlanRepository.findById(planId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
        return healthPlan.getHealthPlanImages().stream()
                .map(HealthPlanImage::getImageUrl)
                .collect(Collectors.toList());
    }

    public ByteArrayResource getHealthPlanImageFile(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();
            return new ByteArrayResource(imageBytes);
        } catch (Exception e) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_IMAGE_NOT_FOUND);
        }
    }

    /**
     * HealthPlan 이미지 일부 변경 (기존 이미지 삭제 후 새 이미지 업로드) - 사용할지 미정
     */
//    public void updateHealthPlanImages(Long planId, List<String> deleteImageUrls, List<String> newImageUrls) {
//        HealthPlan healthPlan = healthPlanRepository.findById(planId)
//                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
//
//        // 기존 이미지 삭제
//        for (String imageUrl : deleteImageUrls) {
//            Optional<HealthPlanImage> imageToDelete = healthPlan.getHealthPlanImages().stream()
//                    .filter(image -> image.getFilePath().equals(imageUrl))
//                    .findFirst();
//
//            imageToDelete.ifPresent(image -> {
//                s3Uploader.deleteFile(image.getFileName());
//                healthPlan.getHealthPlanImages().remove(image);
//                healthPlanImageRepository.delete(image);
//            });
//        }
//
//        // 새 이미지 추가
//        for (String imageUrl : newImageUrls) {
//            String fileName = s3Uploader.extractKeyFromUrl(imageUrl);
//            HealthPlanImage newImage = HealthPlanImage.builder()
//                    .healthPlan(healthPlan)
//                    .filePath(imageUrl)
//                    .fileName(fileName)
//                    .build();
//            healthPlan.getHealthPlanImages().add(newImage);
//            healthPlanImageRepository.save(newImage);
//        }
//    }

    /**
     * 건강 목표 이미지 삭제
     */
    public HealthPlanImage deleteHealthPlanImage(Long imageId) {
        HealthPlanImage image = healthPlanImageRepository.findById(imageId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_IMAGE_NOT_FOUND));

        amazonS3Manager.deleteFile(image.getImageUrl());
        healthPlanImageRepository.delete(image);

        return image;
    }
}


