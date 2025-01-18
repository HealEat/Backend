package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.converter.StoreConverter;
import healeat.server.service.StoreQueryServiceImpl;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.TestResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final StoreQueryServiceImpl storeQueryServiceImpl;

    @Operation(summary = "힐릿 검색 로직 테스트용", description = "쿼리 있든 없든 사용 가능, 필터(최대 5개) 지정 가능")
    @GetMapping("/test")
    public ApiResponse<TestResponseDto> getApiTestResults(
            @ModelAttribute StoreRequestDto.SearchKeywordDto request) {
        List<KakaoPlaceResponseDto.Document> documentsByKeywords = storeQueryServiceImpl.getDocumentsByKeywords(request);
        TestResponseDto response = TestResponseDto.builder()
                .documents(documentsByKeywords)
                .count(documentsByKeywords.size())
                .apiCallCount(storeQueryServiceImpl.getApiCallCount())
                .build();
        return ApiResponse.onSuccess(response);
    }

//    @Operation(summary = "검색 화면", description = "쿼리 스트링으로 검색어와 필터 조건" +
//            "(사용자 위치, 음식 종류/특징 키워드, 최소 별점)을 받아서 가게 목록을 조회합니다.")
//    @GetMapping
//    public ApiResponse<StoreResonseDto.StorePreviewListDto> getSearchResults(
//            @RequestParam Integer page,
//            @ModelAttribute StoreRequestDto.SearchKeywordDto request,
//            @RequestParam Float minRating) {
//
//        Page<KakaoPlaceResponseDto.Document> docPage = storeQueryServiceImpl.getSortedDocuments(page, request, minRating);
//
//        storeQueryServiceImpl.combineResults(docPage);
//
//        return ApiResponse.onSuccess(StoreConverter.toStorePreviewListDto(docPage));
//    }
}


