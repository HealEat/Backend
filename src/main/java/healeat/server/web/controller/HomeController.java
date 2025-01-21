package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
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

    @GetMapping
    @Operation(summary = "홈화면에서 추천 가게 리스트를 조회합니다.",
            description = "홈화면에서 현재 위치를 기반으로 추천 가게 리스트를 조회합니다.")
    public ApiResponse<StoreResonseDto.StorePreviewDtoList> getHomeList(
            @AuthenticationPrincipal Member member,
            @ModelAttribute StoreRequestDto.HealEatRequestDto request){

        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "홈화면에서 추천 가게 미리보기를 조회합니다.",
            description = "홈화면에서 현재 위치를 기반으로 추천된 가게 리스트 중 하나를 선택했을 때 보이는 미리보기 화면입니다.")
    public ApiResponse<StoreResonseDto.HomeStorePreviewDto> getStorePreview(
            @PathVariable Long storeId, @AuthenticationPrincipal Member member){

        return ApiResponse.onSuccess(null);
    }
}
