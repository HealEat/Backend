package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.repository.BookmarkRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.BookmarkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StoreRepository storeRepository;
    private final StoreQueryServiceImpl storeQueryServiceImpl;

    // 북마크를 회원에 저장
    @Transactional
    public Bookmark saveBookmark(Member member, Long storeId) {

        Optional<Store> optionalStore = storeRepository.findById(storeId);

        if (optionalStore.isEmpty()) {
            throw new StoreHandler(ErrorStatus.STORE_NOT_FOUND);
        }

        Store store = optionalStore.get();
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .store(store)
                .build();

        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public BookmarkResponseDto deleteBookmark(Long bookmarkId) {

        Bookmark bookmark = bookmarkRepository.getBookmarkById(bookmarkId);

        BookmarkResponseDto response = BookmarkResponseDto.builder()
                .bookmarkId(bookmark.getId())
                .memberId(bookmark.getMember().getId())
                .placeName(bookmark.getStore().getPlaceName())
                .deletedAt(LocalDateTime.now())
                .build();

        bookmarkRepository.delete(bookmark);

        return response;
    }
}
