package healeat.server.aws.s3;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthPlanHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class S3PresignedUploader {

    private final WebClient webClient;

    public S3PresignedUploader(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void uploadFileToS3(String presignedUrl, Path filePath) throws Exception {
        byte[] fileBytes = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath);

        webClient.put()
                .uri(presignedUrl)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .bodyValue(fileBytes)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> System.out.println("파일 업로드 성공!"))
                .doOnError(this::handleError) // 오류 핸들러를 활용하여 처리
                .subscribe();
    }

    private void handleError(Throwable error) {
        System.err.println("파일 업로드 실패: " + error.getMessage());

        if (error.getMessage().contains("403")) {
            throw new HealthPlanHandler(ErrorStatus._FORBIDDEN);
        } else if (error.getMessage().contains("400")) {
            throw new HealthPlanHandler(ErrorStatus._BAD_REQUEST);
        } else if (error.getMessage().contains("500")) {
            throw new HealthPlanHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}