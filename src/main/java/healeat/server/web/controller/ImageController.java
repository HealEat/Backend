package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ProfileImageConverter;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.repository.MemberRepository;
import healeat.server.service.ImageService;
import healeat.server.web.dto.ImageResponseDto;
import healeat.server.web.dto.ProfileImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ProfileImageConverter profileImageConverter;

    /***************************** 프로필 이미지 *****************************/

    @Operation(summary = "프로필 이미지 조회", description = "해당 사용자의 프로필 이미지를 조회합니다."
            + "(이미지 형태 그대로 반환)")
    @GetMapping("/profile-image")
    public ResponseEntity<Resource> getProfileImage(
            @AuthenticationPrincipal Member member) {

        return imageService.getProfileImage(member.getId());
    }

    @Operation(summary = "프로필 이미지 삭제", description = "해당 사용자의 프로필 이미지를 삭제합니다."
    + "기본적으로는 프로필 수정 API 를 사용한다고 생각하고 필요하면 쓰시면 될 것 같습니다.")
    @DeleteMapping("/profile-image")
    public ApiResponse<ProfileImageResponseDto> deleteProfileImage(
            @AuthenticationPrincipal Member member) {

        Member deleteImageMember = imageService.deleteProfileImage(member.getId());
        return ApiResponse.onSuccess(profileImageConverter.toResponseDto(deleteImageMember));
    }

    /***************************** 리뷰 이미지 *****************************/

    @Operation(summary = "리뷰 이미지 조회", description = "해당 리뷰 이미지를 조회합니다."
            + "(이미지 형태 그대로 반환)")
    @GetMapping("/review-image/{imageId}")
    public ResponseEntity<Resource> getReviewImage(
            @PathVariable Long imageId) {

        return imageService.getReviewImage(imageId);
    }

    @Operation(summary = "리뷰 이미지 삭제", description = "해당 리뷰 이미지를 삭제합니다."
            + "리뷰 삭제 기능은 없는 걸로 알고 있는데 필요하면 쓰는 목적입니다.")
    @DeleteMapping("/review-image/{imageId}")
    public ApiResponse<ImageResponseDto.publicUrlDto> deleteReviewImage(
            @PathVariable Long imageId) {

        ReviewImage deleteReviewImage = imageService.deleteReviewImage(imageId);
        return ApiResponse.onSuccess(ReviewConverter.toReviewImage(deleteReviewImage));
    }
}
