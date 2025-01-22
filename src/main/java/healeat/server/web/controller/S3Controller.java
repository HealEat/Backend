package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.S3Service;
import healeat.server.web.dto.ImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping(value = "/posturl")
    public ApiResponse<ImageResponseDto.GetS3UrlDto> getPostS3Url(
            @AuthenticationPrincipal Member member, String filename) {
        return ApiResponse.onSuccess(s3Service.getPostS3Url(member.getId(), filename));
    }

    @GetMapping(value = "/geturl")
    public ApiResponse<ImageResponseDto.GetS3UrlDto> getGetS3Url(
            @AuthenticationPrincipal Member member, @RequestParam String key) {
        return ApiResponse.onSuccess(s3Service.getGetS3Url(member.getId(), key));
    }
}
