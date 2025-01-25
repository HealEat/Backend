package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.aws.s3.S3Uploader;
import healeat.server.domain.HealthPlan;
import healeat.server.domain.Member;
import healeat.server.web.dto.HealthPlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/URL")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    @Operation(summary = "Presigned URL 리스트 조회", description = "이미지를 업로드하기 위한 Presigned URL 을 조회합니다")
    @GetMapping("/upload-urls")
    public ApiResponse<List<String>> uploadUrls(@AuthenticationPrincipal final Member member) {

        return null;
    }

    @Operation(summary = "Pulbic URL 리스트 조회", description = "이미지를 조회하기 위한 Public URL 을 조회합니다")
    @GetMapping("/public-urls")
    public ApiResponse<List<String>> readUrls(@AuthenticationPrincipal final Member member) {

        return null;
    }

}
