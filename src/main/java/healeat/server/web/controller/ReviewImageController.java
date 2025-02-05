package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.ReviewImage;
import healeat.server.service.ReviewImageService;
import healeat.server.web.dto.ImageResponseDto;
import healeat.server.web.dto.ReviewRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/review-image")
@RequiredArgsConstructor
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @Operation(summary = "리뷰 이미지 업로드", description = "최대 10개의 리뷰 이미지를 Presigned URL을 통해 S3에 업로드합니다.",
            requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/{reviewId}/upload", consumes = "multipart/form-data")
    public ApiResponse<List<ImageResponseDto.PresignedUrlDto>> uploadReviewImages(
            @PathVariable Long reviewId,
            @RequestPart(name = "files") List<MultipartFile> files,
            @RequestPart(name = "requests") List<ReviewRequestDto.ReviewImageRequestDto> requests) throws Exception {

        return ApiResponse.onSuccess(reviewImageService.uploadReviewImages(reviewId, files, requests));
    }

    @Operation(summary = "리뷰 이미지 미리 보기", description = "S3에 저장된 리뷰 이미지를 직접 반환합니다.")
    @GetMapping("/view")
    public ResponseEntity<ByteArrayResource> viewReviewImage(@RequestParam String imageUrl) {
        return reviewImageService.getReviewImageFile(imageUrl);
    }

    @Operation(summary = "리뷰 이미지 삭제", description = "리뷰 이미지를 S3에서 삭제합니다.")
    @DeleteMapping("/{imageId}")
    public ApiResponse<ImageResponseDto.publicUrlDto> deleteReviewImage(@PathVariable Long imageId) {

        ReviewImage deleteImage = reviewImageService.deleteReviewImage(imageId);
        return ApiResponse.onSuccess(ReviewConverter.toReviewImage(deleteImage));
    }
}
