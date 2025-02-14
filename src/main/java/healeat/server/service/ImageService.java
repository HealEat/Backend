package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.apiPayload.exception.handler.ReviewHandler;
import healeat.server.aws.s3.AmazonS3Manager;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.repository.MemberRepository;
import healeat.server.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final AmazonS3Manager amazonS3Manager;
    private final MemberRepository memberRepository;
    private final ReviewImageRepository reviewImageRepository;

    /***************************** 프로필 이미지만을 위한 메서드 *****************************/

    @Transactional
    public ResponseEntity<Resource> getProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String profileImageUrl = member.getProfileImageUrl();
        if (profileImageUrl == null) {
            throw new MemberHandler(ErrorStatus.PROFILE_IMAGE_NOT_FOUND);
        }

        try {
            URL url = new URL(profileImageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 필요하면 다른 MIME 타입으로 변경 가능
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-image.jpg\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("이미지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public Member deleteProfileImage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String profileImageUrl = member.getProfileImageUrl();
        if (profileImageUrl != null) {
            amazonS3Manager.deleteFile(profileImageUrl);
            member.updateProfileImageUrl(null);
            memberRepository.save(member);
        }
        return member;
    }

    /***************************** 리뷰 이미지만을 위한 메서드 *****************************/

    @Transactional
    public ResponseEntity<Resource> getReviewImage(Long reviewImageId) {
        ReviewImage reviewImage = reviewImageRepository.findById(reviewImageId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_IMAGE_NOT_FOUND));

        String reviewImageUrl = reviewImage.getImageUrl();

        try {
            URL url = new URL(reviewImageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 필요하면 다른 MIME 타입으로 변경 가능
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"review-image.jpg\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("이미지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public ReviewImage deleteReviewImage(Long reviewImageId) {
        ReviewImage reviewImage = reviewImageRepository.findById(reviewImageId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_IMAGE_NOT_FOUND));

        String reviewImageUrl = reviewImage.getImageUrl();
        amazonS3Manager.deleteFile(reviewImageUrl);
        reviewImageRepository.delete(reviewImage);

        return reviewImage;
    }
}
