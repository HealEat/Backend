package healeat.server.converter;

import healeat.server.domain.mapping.Bookmark;
import healeat.server.web.dto.BookmarkResponseDto;

import java.time.LocalDateTime;

public class BookmarkConverter {

    public static BookmarkResponseDto toSetResponseDto(Bookmark bookmark) {

        return BookmarkResponseDto.builder()
                .bookmarkId(bookmark.getId())
                .memberId(bookmark.getMember().getId())
                .placeName(bookmark.getStore().getPlaceName())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
