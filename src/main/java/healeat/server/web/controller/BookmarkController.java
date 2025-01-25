package healeat.server.web.controller;

import healeat.server.apiPayload.ApiResponse;
import healeat.server.domain.Member;
import healeat.server.repository.MemberRepository;
import healeat.server.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final MemberRepository memberRepository;

    @Operation(summary = "가게 북마크 저장 API", description = "회원의 가게 북마크에 저장합니다." +
            " 만약, DB에 존재하지 않았던 가게에 북마크를 하면 새로 저장합니다.")
    @PutMapping("/{storeId}")
    public ApiResponse<Void> toggleBookmark(
            @AuthenticationPrincipal Member member,
            @PathVariable Long storeId) {

        Member testMember = memberRepository.findById(999L).get();

        bookmarkService.saveBookmark(testMember, storeId);
        return ApiResponse.onSuccess(null);
    }

//    @Operation(summary = "저장한 가게 북마크 목록 조회 API-(마이페이지)")
//    @GetMapping
//    public ApiResponse<List<StoreResonseDto.StorePreviewDto>> getBookmarkedStores(
//            @AuthenticationPrincipal Member member) {
//        List<StoreResonseDto.StorePreviewDto> stores = bookmarkService.getBookmarkedStores(member);
//        return ApiResponse.onSuccess(stores);
//    }

}
