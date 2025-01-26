package healeat.server.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.S3Handler;
import healeat.server.web.dto.ImageResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Uploader {

    private static AmazonS3 amazonS3;

    @Autowired
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketValue;

    @Value("${cloud.aws.region.static}")
    private Region regionValue;

    private static String bucket;
    private static Region region;

    private final String BUCKET_DOMAIN = "https://" + bucket +".s3." + region + ".amazonaws.com/";

    @PostConstruct
    public void init(){
        bucket = bucketValue;
        region = regionValue;
    }

    /**
     * presigned URL을 생성하여 S3에 이미지 업로드를 지원하는 메소드
     * @param imageType 이미지 저장 폴더 유형 (reviews, profiles, health-plans)
     * @param imageExtension 이미지 확장자
     * @return upload URL과 public URL이 담긴 Map
     */
    public ImageResponseDto.PresignedUrlDto createPresignedUrl(String imageType, String imageExtension) {

        // 폴더별로 경로 설정
        String folder;
        switch (imageType.toLowerCase()) {
            case "reviews":
                folder = "reviews/";
                break;
            case "profiles":
                folder = "profiles/";
                break;
            case "health-plans":
                folder = "health-plans/";
                break;
            default:
                throw new S3Handler(ErrorStatus.IMAGE_TYPE_NOT_FOUND);
        }

        String keyName = folder + UUID.randomUUID().toString().replace("-", "") + "." + imageExtension;
        String contentType = "image/" + imageExtension;
        Map<String, String> metadata = Map.of(
                "fileType", contentType,
                "Content-Type", contentType
        );

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(keyName)
                .metadata(metadata)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // The URL expires in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String uploadUrl = presignedRequest.url().toString();
        String publicUrl = BUCKET_DOMAIN + keyName;

        log.info("Presigned URL to upload a file to: {}", uploadUrl);
        log.info("HTTP method: {}", presignedRequest.httpRequest().method());

        return ImageResponseDto.PresignedUrlDto.builder()
                .presignedUrl(uploadUrl)
                .publicUrl(publicUrl)
                .build();
    }

    public void deleteObject(String key) {
        amazonS3.deleteObject(bucket, key);
        log.info("Deleted object from S3: {}", key);
    }

    //KeyName 추출 메서드
    public String extractKeyFromUrl(String publicUrl) {
        if (publicUrl.startsWith(BUCKET_DOMAIN)) {
            return publicUrl.substring(BUCKET_DOMAIN.length());
        } else {
            throw new S3Handler(ErrorStatus.IMAGE_INVALID_PUBLIC_URL);
        }
    }

}
