package healeat.server.web.dto.api_response;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoCoordResponseDto {

    List<Document> documents;

    @Getter
    public static class Document {

        String x;
        String y;

        Address address;
    }

    @Getter
    public static class Address {

        String region_3depth_name;

        String region_3depth_h_name;
    }
}
