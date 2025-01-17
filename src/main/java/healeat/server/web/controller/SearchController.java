package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Store;
import healeat.server.service.StoreQueryService;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final StoreQueryService storeQueryService;

    @Operation(summary = "검색 화면", description = "쿼리 스트링으로 검색어와 필터 조건" +
            "(사용자 위치, 음식 종류/특징 키워드, 최소 별점)을 받아서 가게 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<StoreResonseDto.StorePreviewListDto> getSearchResults(
            @ModelAttribute StoreRequestDto.SearchFilterDto request) {

//        Page<Store> searchPage = storeQueryService.searchByFilter(request);

        return ApiResponse.onSuccess(StoreConverter.toStorePreviewListDto(/*searchPage*/null));
    }

}


