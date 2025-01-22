package healeat.server.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import healeat.server.web.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;

    // 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageResponseDto.GetS3UrlDto getPostS3Url(Long memberId, String filename) {
        // filename 설정하기(profile 경로 + 멤버ID + 랜덤 값)
        String key = "profile/" + memberId + "/" + UUID.randomUUID() + "/" + filename;

        // url 유효기간 설정하기(1시간)
        Date expiration = getExpiration();

        // presigned url 생성하기
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getPostGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        // return
        return ImageResponseDto.GetS3UrlDto.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    public ImageResponseDto.GetS3UrlDto getGetS3Url(Long memberId, String key) {
        // url 유효기간 설정하기(1시간)
        Date expiration = getExpiration();

        // presigned url 생성하기
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getGetGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        // return
        return ImageResponseDto.GetS3UrlDto.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    /* post 용 URL 생성하는 메소드 */
    private GeneratePresignedUrlRequest getPostGeneratePresignedUrlRequest(String fileName, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest
                = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withKey(fileName)
                .withExpiration(expiration);
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    /* get 용 URL 생성하는 메소드 */
    private GeneratePresignedUrlRequest getGetGeneratePresignedUrlRequest(String key, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
    }

    private static Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60; // 1시간으로 설정하기
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
