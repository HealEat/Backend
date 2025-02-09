package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.web.dto.*;
import healeat.server.web.dto.HealInfoResponseDto.ChooseResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import static healeat.server.converter.MemberHealQuestionConverter.*;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final MemberService memberService;
    private final MemberHealthInfoService memberHealthInfoService;
    private final MemberRepository memberRepository;


    @Operation(summary = "프로필 설정 API", description = "프로필 이미지와 닉네임을 설정합니다",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberProfileResponseDto> createProfile(
            @AuthenticationPrincipal Member member,
            @RequestPart(name = "file", required = false)
            MultipartFile file,
            @RequestPart(name = "request", required = true)
            MemberProfileRequestDto request) {

        Member testMember = memberRepository.findById(999L).get();
        Member profileMember = memberService.createProfile(testMember, file, request);

        return ApiResponse.onSuccess(MemberProfileResponseDto.from(profileMember));
    }

    @Operation(summary = "질병 검색 API")
    @GetMapping("/disease/search")
    public ApiResponse<List<Disease>> searchDiseases(@RequestParam String keyword) {
        List<Disease> diseases = memberHealthInfoService.searchDiseases(keyword);
        return ApiResponse.onSuccess(diseases);
    }

    @Operation(summary = "회원 질병 저장 API")
    @PatchMapping("/member/disease")
    public ApiResponse<Void> saveDiseasesToMember(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberDiseaseRequestDto request) {

        memberService.saveDiseasesToMember(member, request.getDiseaseIds());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "베지테리언 선택 API")
    @PatchMapping("/veget")
    public ApiResponse<ChooseResultDto> chooseVegetarian(
            @AuthenticationPrincipal Member member,
            @RequestParam String vegetarian) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(toChooseVegetResult(
                memberHealthInfoService.chooseVegetarian(testMember, vegetarian)));
    }

    @Operation(summary = "다이어트 선택 API")
    @PatchMapping("/diet")
    public ApiResponse<ChooseResultDto> chooseDiet(
                @AuthenticationPrincipal Member member,
                @RequestParam String diet) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(toChooseDietResult(
                memberHealthInfoService.chooseDiet(testMember, diet)));
    }

    @Operation(summary = "기본 질문의 답변 저장 API", description =
            """
                    1. 질병으로 인해 겪는 건강 상의 불편함은 무엇인가요?
                    2. 건강 관리를 위해 필요한 식사는 무엇인가요?
                    3. 건강 관리를 위해 특별히 필요한 영양소가 있나요?
                    4. 건강 관리를 위해 피해야 하는 음식이 있나요?""")
    @PostMapping("/{questionNum}")
    public ApiResponse<HealInfoResponseDto.BaseResultDto> saveAnswer(
            @AuthenticationPrincipal Member member,
            @PathVariable Integer questionNum,
            @RequestBody AnswerRequestDto request) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(toBaseResult(
                memberHealthInfoService.createQuestion(testMember, questionNum, request)));
    }

    @Operation(summary = "알고리즘 계산 API", description = "알고리즘을 통해 healEatFoods(추천 음식 카테고리 리스트)를" +
            " 멤버에 저장합니다. 건강 정보 최초 설정이 완료된 후 로딩 페이지에 적절한 API입니다.")
    @PatchMapping("/loading")
    public ApiResponse<HealInfoResponseDto> calculateHealEat(@AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(memberHealthInfoService.makeHealEat(testMember));
    }
}
