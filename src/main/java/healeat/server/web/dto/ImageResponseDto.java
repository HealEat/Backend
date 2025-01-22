package healeat.server.web.dto;

import lombok.*;

public class ImageResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public class GetS3UrlDto {

        private String preSignedUrl;
        private String key;
    }
}
