package healeat.server.web.dto.api_response;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoAddressResponse {

    List<Document> documents;

    @Getter
    public static class Document {

        Address address;
    }

    @Getter
    public static class Address {

        String address_name;
        String region_3depth_name;
    }
}
