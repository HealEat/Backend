package healeat.server.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.S3Handler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AmazonConfig {


    private AWSCredentials awsCredentials;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.path.profile}")
    private String profilePath;

    @Value("${cloud.aws.s3.path.healthPlan}")
    private String healthPlanPath;

    @Value("${cloud.aws.s3.path.review}")
    private String reviewPath;

    private static String BUCKET_DOMAIN;

    @PostConstruct
    public void init() {
        this.awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        BUCKET_DOMAIN = "https://" + bucket +".s3." + region + ".amazonaws.com/";
    }

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(awsCredentials);
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

