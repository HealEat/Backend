package healeat.server.web.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemoImageResponseDto {
    private Long id;
    private String imageUrl;
}
