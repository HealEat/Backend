package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.apiPayload.exception.handler.ReviewHandler;
import healeat.server.aws.s3.S3PresignedUploader;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.MemberRepository;
import healeat.server.repository.ReviewImageRepository;
import healeat.server.repository.ReviewRepository;
import healeat.server.web.dto.ImageResponseDto;
import healeat.server.web.dto.ProfileImageRequestDto;
import healeat.server.web.dto.ReviewRequestDto;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewImageService {

    private final S3Uploader s3Uploader;
    private final S3PresignedUploader s3PresignedUploader;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public List<ImageResponseDto.PresignedUrlDto> uploadReviewImages(Long reviewId, List<MultipartFile> files, List<ReviewRequestDto.ReviewImageRequestDto> requests) throws Exception {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));

        if (review.getReviewImageList().size() + requests.size() > 10) {
            throw new ReviewHandler(ErrorStatus.REVIEW_TOO_MANY_IMAGES);
        }

        List<ImageResponseDto.PresignedUrlDto> presignedUrls = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ReviewRequestDto.ReviewImageRequestDto request = requests.get(i);
            MultipartFile file = files.get(i);

            ImageResponseDto.PresignedUrlDto urlDto = s3Uploader.createPresignedUrl("reviews", request.getImageExtension());
            presignedUrls.add(urlDto);

            Path tempFilePath = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFilePath.toFile());

            // S3에 Presigned URL을 이용하여 직접 업로드
            s3PresignedUploader.uploadFileToS3(urlDto.getPresignedUrl(), tempFilePath);
            Files.delete(tempFilePath);

            ReviewImage reviewImage = ReviewImage.builder()
                    .review(review)
                    .filePath(urlDto.getPublicUrl())
                    .fileName(s3Uploader.extractKeyFromUrl(urlDto.getPublicUrl()))
                    .build();

            review.getReviewImageList().add(reviewImage);
            reviewImageRepository.save(reviewImage);
        }

        return presignedUrls;
    }

    public List<String> getReviewImageUrls(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));

        return review.getReviewImageList().stream()
                .map(ReviewImage::getFilePath)
                .collect(Collectors.toList());
    }

    public ResponseEntity<ByteArrayResource> getReviewImageFile(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 기본 JPEG, 필요 시 변경 가능
                    .body(resource);
        } catch (Exception e) {
            throw new ReviewHandler(ErrorStatus.REVIEW_IMAGE_NOT_FOUND);
        }
    }

    @Transactional
    public ReviewImage deleteReviewImage(Long imageId) {
        ReviewImage reviewImage = reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_IMAGE_NOT_FOUND));

        s3Uploader.deleteFile(reviewImage.getFileName());
        reviewImageRepository.delete(reviewImage);

        return reviewImage;
    }
}
