package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.MemberHealQuestionConverter;
import healeat.server.domain.Disease;
import healeat.server.domain.Member;
import healeat.server.service.DiseaseService;
import healeat.server.repository.MemberRepository;
import healeat.server.service.MemberHealthInfoService;
import healeat.server.service.MemberService;
import healeat.server.web.dto.*;
import healeat.server.web.dto.HealInfoResponseDto.ChangeBaseResultDto;
import healeat.server.web.dto.HealInfoResponseDto.ChooseResultDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static healeat.server.converter.MemberHealQuestionConverter.*;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final MemberService memberService;
    private final MemberHealthInfoService memberHealthInfoService;
    private final DiseaseService diseaseService;
    private final MemberRepository memberRepository;


    @Operation(summary = "프로필 설정 API")
    @PostMapping("/profile")
    public ApiResponse<MemberProfileResponseDto> createProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberProfileRequestDto request) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(memberService.createProfile(testMember, request));
    }

    @Operation(summary = "질병 검색 API")
    @GetMapping("/disease/search")
    public ApiResponse<List<Disease>> searchDiseases(@RequestParam String keyword) {
        List<Disease> diseases = memberHealthInfoService.searchDiseases(keyword);
        return ApiResponse.onSuccess(diseases);
    }

    @Operation(summary = "회원 질병 저장 API")
    @PostMapping("/disease/save")
    public ApiResponse<Void> saveDiseases(
            @AuthenticationPrincipal Member member,
            @RequestBody MemberDiseaseRequestDto request
    ) {
        memberHealthInfoService.saveMemberDiseases(member, request.getDiseaseIds());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "질환 정보 CSV 저장 API"
            , description = "새로운 데이터 갱신이 필요할 경우, 이 메소드를 다시 호출하여 CSV 파일을 다시 업로드")
    @PostMapping("/disease/upload")
    public ApiResponse<Void> uploadDiseases(@RequestParam String filePath) {
        diseaseService.saveDiseasesFromCSV(filePath);
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
    @GetMapping("/loading")
    public ApiResponse<HealInfoResponseDto> calculateHealEat(@AuthenticationPrincipal Member member) {

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(memberHealthInfoService.makeHealEat(testMember));
    }
}
