package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.HealthPlanImage;
import healeat.server.domain.Member;
import healeat.server.repository.HealthPlanImageRepository;
import healeat.server.repository.HealthPlanRepository;
import healeat.server.web.dto.HealthPlanRequestDto;
import healeat.server.web.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HealthPlanService {

    private final S3Uploader s3Uploader;
    private final HealthPlanRepository healthPlanRepository;
    private final HealthPlanImageRepository healthPlanImageRepository;

    public List<HealthPlan> getAllHealthPlans() {
        return healthPlanRepository.findAll();
    }

    //혹시나 필요하면 쓸 getById
    public HealthPlan getHealthPlanById(Long id) {
        return healthPlanRepository.findById(id).orElseThrow(() ->
                new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));
    }

    public List<HealthPlan> getHealthPlanByMemberId(Long memberId) {
        return healthPlanRepository.findByMemberId(memberId);
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

    public List<HealthPlanImage> getHealthPlanImageByPlanId(Long planId) {
        return healthPlanImageRepository.findAllByHealthPlanId(planId);
    }

    @Transactional
    public List<ImageResponseDto.PresignedUrlDto> generateImageUrls(
            Long planId,
            List<HealthPlanRequestDto.HealthPlanImageRequestDto> requests
    ) {
        // HealthPlan 엔티티 가져오기
        HealthPlan healthPlan = healthPlanRepository.findById(planId)
                .orElseThrow(() -> new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_NOT_FOUND));

        // 기존 이미지가 5개를 초과하지 않도록 검증
        if (healthPlan.getHealthPlanImages().size() + requests.size() > 5) {
            throw new HealthPlanHandler(ErrorStatus.HEALTH_PLAN_TOO_MANY_IMAGES);
        }

        // 요청에 따라 Presigned URL 및 Public URL 생성
        List<ImageResponseDto.PresignedUrlDto> presignedUrls = new ArrayList<>();
        for (HealthPlanRequestDto.HealthPlanImageRequestDto request : requests) {
            ImageResponseDto.PresignedUrlDto urlMap = s3Uploader.createPresignedUrl(request.getImageType(), request.getImageExtension());
            presignedUrls.add(urlMap);

            String fileName = s3Uploader.extractKeyFromUrl(urlMap.getPublicUrl());

            // HealthPlanImage 엔티티 생성
            HealthPlanImage healthPlanImage = HealthPlanImage.builder()
                    .healthPlan(healthPlan)
                    .filePath(urlMap.getPublicUrl())
                    .fileName(fileName)
                    .build();

            // HealthPlan에 이미지 추가
            healthPlan.getHealthPlanImages().add(healthPlanImage);
        }

        // DB에 수정된 HealthPlan 저장
        healthPlanRepository.save(healthPlan);

        return presignedUrls;
    }
}


