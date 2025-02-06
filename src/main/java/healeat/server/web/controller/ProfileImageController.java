package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ProfileImageConverter;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.ProfileImageService;
import healeat.server.web.dto.ProfileImageRequestDto;
import healeat.server.web.dto.ProfileImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile-image")
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;
    private final ProfileImageConverter profileImageConverter;
    //테스트용 멤버
    private final MemberRepository memberRepository;

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 Presigned URL을 이용하여 S3에도 업로드합니다."
            + " 이미지 확장자만 입력",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileImageResponseDto> uploadProfileImage(
            @AuthenticationPrincipal Member member,
            @RequestPart(name = "file", required = true)
            MultipartFile file) {

        Member testMember = memberRepository.findById(999L).get();

        Member uploadedMember = profileImageService.uploadProfileImage(testMember.getId(), file);
        return ApiResponse.onSuccess(profileImageConverter.toResponseDto(uploadedMember));
    }

    @Operation(summary = "프로필 이미지 조회", description = "해당 사용자의 프로필 이미지를 조회합니다."
            + "(이미지 형태 그대로 반환)")
    @GetMapping
    public ResponseEntity<Resource> getProfileImage(
            @AuthenticationPrincipal Member member) {
        Member testMember = memberRepository.findById(999L).get();

        return profileImageService.getProfileImage(testMember.getId());
    }

    @Operation(summary = "프로필 이미지 삭제", description = "해당 사용자의 프로필 이미지를 삭제합니다.")
    @DeleteMapping
    public ApiResponse<ProfileImageResponseDto> deleteProfileImage(
            @AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();
        Member deleteImageMember = profileImageService.deleteProfileImage(testMember.getId());
        return ApiResponse.onSuccess(profileImageConverter.toResponseDto(deleteImageMember));
    }
}
