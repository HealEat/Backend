package healeat.server.web.dto.apiResponse;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoPlaceResponseDto {

    List<Document> documents;
    Meta meta;

    @Getter
    public static class Document {

        String id;
        String place_name;
        String category_name;
        String phone;
        String address_name;
        String road_address_name;
        String x;
        String y;
        String place_url;
        String distance;
    }

    @Getter
    public static class Meta {
        Integer total_count;
        Integer pageable_count;
        Boolean is_end;

        SameName same_name;
    }

    @Getter
    public static class SameName {

        // 질의어에서 지역 정보를 제외한 키워드
        // 예: "중앙로 맛집" 검색 시, "맛집"이 저장됨.
        String keyword;

        // 검색어에 지역이 포함될 때, 현재 검색에 사용된 지역
        // 예: "중앙로 맛집" 검색 시, 사용자에게 가까운 "중앙로"가 (변환되어) 저장됨.
        String selected_region;

        // 동명의 다른 지역 리스트
        List<String> region;
    }
}
