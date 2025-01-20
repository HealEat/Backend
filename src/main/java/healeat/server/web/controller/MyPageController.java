package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.mapping.Review;
import healeat.server.service.MemberService;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.MemberProfileResponseDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/health-info")
    @Operation(summary = "마이페이지 나의 건강정보 조회 API",
            description = "마이페이지에서 나의 건강정보를 전체 조회할 수 있습니다.")
    public ApiResponse<MemberProfileResponseDto.MyHealthProfileDto> getMyHealthInfo(
            @AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(null);
    }
}
