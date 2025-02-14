package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.StoreCommandService;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
                    - 페이징이 적용됩니다.(페이지 당 10개)
                    - 동일한 위치(오차 범위 200m 이내) 및 반경(오차 범위 200m 이내)에서 캐시된 결과가 반환됩니다.""")
    @PostMapping
    public ApiResponse<StoreResponseDto.StorePreviewDtoList> getHomeList(
            @AuthenticationPrincipal Member member,
            @RequestParam Integer page,
            @RequestBody StoreRequestDto.HealEatRequestDto request){

        if(member == null) {
            // 로그인하지 않은 경우, 추천 리스트 없이 응답 반환
            return ApiResponse.onSuccess(new StoreResponseDto.StorePreviewDtoList(List.of(),0, 0, 0L, true, true, null));
        }
        return ApiResponse.onSuccess(storeCommandService.recommendAndMapStores(member, page, request));
    }
}
