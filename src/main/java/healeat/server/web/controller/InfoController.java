package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.HealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final MemberService memberService;
    private final HealthInfoService healthInfoService;

    @Operation(summary = "프로필 설정 API")
    @PostMapping("/profile")
    public ApiResponse<MemberProfileResponseDto> createProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberProfileRequestDto request) {

        return ApiResponse.onSuccess(/*memberService.createProfile(member, request)*/null);
    }

    @Operation(summary = "질환 선택")
    @PostMapping("/disease")
    public ApiResponse<Void> saveDiseases() {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "베지테리언 선택")
    @PostMapping("/veget")
    public ApiResponse<AnswerResponseDto> chooseVegetarian(
            @AuthenticationPrincipal Member member,
            @RequestBody AnswerRequestDto request) {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "다이어트 선택")
    @PostMapping("/diet")
    public ApiResponse<AnswerResponseDto> chooseDiet(
                @AuthenticationPrincipal Member member,
                @RequestBody AnswerRequestDto request) {

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "기본 질문의 답변 저장하기 API", description =
            """
                    1. 질병으로 인해 겪는 건강 상의 불편함은 무엇인가요?
                    2. 건강 관리를 위해 필요한 식사는 무엇인가요?
                    3. 건강 관리를 위해 특별히 필요한 영양소가 있나요?
                    4. 건강 관리를 위해 피해야 하는 음식이 있나요?""")
    @PostMapping("/{questionNum}")
    public ApiResponse<AnswerResponseDto> saveAnswer(
            @AuthenticationPrincipal Member member,
            @PathVariable Integer questionNum,
            @RequestBody AnswerRequestDto request) {

        return ApiResponse.onSuccess(
                healthInfoService.createQuestion(member, questionNum, request));
    }

    @Operation(summary = "알고리즘 계산 API", description = "알고리즘을 통해 추천 음식 카테고리 목록을 멤버에 저장합니다." +
            "건강 정보가 새로 저장되거나, 업데이트될 때 사용하는 API입니다.")
    @GetMapping("/loading")
    public ApiResponse<Void> calculateHealEat(@AuthenticationPrincipal Member member) {

        return ApiResponse.onSuccess(null);
    }
}
