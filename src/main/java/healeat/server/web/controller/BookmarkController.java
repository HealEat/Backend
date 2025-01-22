package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.service.BookmarkService;
import healeat.server.web.dto.StoreResonseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "가게 북마크 추가/삭제 토글 API")
    @PutMapping("/{storeId}")
    public ApiResponse<Void> toggleBookmark(
            @AuthenticationPrincipal Member member,
            @PathVariable("storeId") Long storeId) {
        bookmarkService.toggleBookmark(member, storeId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "저장한 가게 북마크 목록 조회 API-(마이페이지)")
    @GetMapping
    public ApiResponse<List<StoreResonseDto.StorePreviewDto>> getBookmarkedStores(
            @AuthenticationPrincipal Member member) {
        List<StoreResonseDto.StorePreviewDto> stores = bookmarkService.getBookmarkedStores(member);
        return ApiResponse.onSuccess(stores);
    }

}
