package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkResponseDto {

    Long bookmarkId;
    Long memberId;
    String placeName;
    LocalDateTime createdAt;
    LocalDateTime deletedAt;
}
