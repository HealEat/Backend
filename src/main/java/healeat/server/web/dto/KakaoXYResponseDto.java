package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoXYResponseDto {

    List<Document> documents;

    @Getter
    public static class Document {

        String x;
        String y;
    }
}
