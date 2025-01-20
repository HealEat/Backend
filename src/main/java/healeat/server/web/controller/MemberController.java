package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.MemberService;
import healeat.server.web.dto.MemberProfileRequestDto;
import healeat.server.web.dto.MemberProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members/profile")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "프로필 정보 조회 API")
    @GetMapping
    public ApiResponse<MemberProfileResponseDto> getProfileInfo(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(memberService.getProfileInfo(member));
    }

    @Operation(summary = "프로필 설정(생성) API")
    @PostMapping
    public ApiResponse<MemberProfileResponseDto> createProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberProfileRequestDto request) {
        return ApiResponse.onSuccess(memberService.createProfile(member, request));
    }

    @Operation(summary = "프로필 수정 API")
    @PatchMapping
    public ApiResponse<MemberProfileResponseDto> updateProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberProfileRequestDto request) {
        return ApiResponse.onSuccess(memberService.updateProfile(member, request));
    }

    @Operation(summary = "프로필 이미지 설정 API")
    @PostMapping("/profile_image")
    public ApiResponse<Void> setProfileImage(
            @AuthenticationPrincipal Member member,
            @RequestParam("profileImageUrl") String profileImageUrl) {
        memberService.setProfileImage(member, profileImageUrl);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "프로필 이미지 삭제 API")
    @DeleteMapping("/profile_image")
    public ApiResponse<Void> deleteProfileImage(@AuthenticationPrincipal Member member) {
        memberService.deleteProfileImage(member);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "닉네임 중복 확인 API")
    @GetMapping("/check_name")
    public ApiResponse<Boolean> checkName(@RequestParam("name") String name) {
        return ApiResponse.onSuccess(memberService.checkNameAvailability(name));
    }
}
