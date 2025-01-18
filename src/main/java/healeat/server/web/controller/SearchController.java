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

    @Operation(summary = "힐릿 검색 로직 테스트용", description = "1. 쿼리 있든 없든 사용 가능" +
            "\n2. x, y 좌표값 String - 빈칸 또는 지정(nullable)" +
            "\n3. 2개 IdList 0 지우고 지정(nullable, 최대 5개)\n" + "\n\n위 형태로 만들어서 진행 할 것")
    @GetMapping("/test") // 테스트용이라 컨트롤러에서 로직 존재
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


