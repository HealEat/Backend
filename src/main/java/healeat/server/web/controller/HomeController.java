package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.StoreCommandService;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final StoreCommandService storeCommandService;

    @Operation(summary = "홈 화면에서 추천 가게 리스트를 조회합니다.", description =
            """ 
                    Request Body에
                    사용자 x, y, 조사할 반경(radius)를 받아서 추천 가게 목록을 조회합니다.
                    
                    위치 또는 반경의 200m 내 오차까지 동일한 캐시에서 반환됩니다.""")
    @PostMapping
    public ApiResponse<StoreResonseDto.StorePreviewDtoList> getHomeList(
            @AuthenticationPrincipal Member member,
            @RequestParam Integer page,
            @RequestBody StoreRequestDto.HealEatRequestDto request){

        Member testMember = memberRepository.findById(999L).get();

        return ApiResponse.onSuccess(storeCommandService.recommendAndMapStores(testMember, page, request));
    }
}
