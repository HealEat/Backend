package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoPlaceResponseDto {

    List<Document> documents;
    Meta meta;

//    @Override
//    public String toString() {
//
//        return "KakaoPlaceResponse [" +
//                "documents=" + documents + "," +
//                "meta=" + meta +
//                "\n]";
//    }

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

        @Override
        public String toString() {
            return "\nDocument [" +
                    "\nid=" + id + ", " +
                    "\nplace_name=" + place_name + "," +
                    "\ncategory_name=" + category_name + ", " +
                    "\nphone=" + phone + ", " +
                    "\naddress_name=" + address_name +
                    "\nroad_address_name=" + road_address_name + ", " +
                    "\nx=" + x + ", " +
                    "\ny=" + y + ", " +
                    "\nplace_url=" + place_url + ", " +
                    "\ndistance=" + distance +
                    "\n]";
        }
    }

    @Getter
    public static class Meta {
        Integer total_count;
        Integer pageable_count;
        Boolean is_end;

        SameName same_name;

//        @Override
//        public String toString() {
//            return "\nMeta [" +
//                    "\ntotal_count=" + total_count + ", " +
//                    "\npageable_count=" + pageable_count + ", " +
//                    "\nis_end=" + is_end + ", " +
//                    "same_name=" + same_name +
//                    "\n]";
//        }
    }

    @Getter
    public static class SameName {

        // 질의어에서 지역 정보를 제외한 키워드
        // 예: "중앙로 맛집" 검색 시, "맛집"이 저장됨.
        String keyword;

        // 검색어에 지역이 포함될 때, 현재 검색에 사용된 지역
        // 예: "중앙로 맛집" 검색 시, 사용자에게 가까운 "중앙로"가 (변환되어) 저장됨.
        String selected_region;

//        @Override
//        public String toString() {
//            return "\nSameName [" +
//                    "\nkeyword=" + keyword + ", " +
//                    "\nselected_region=" + selected_region +
//                    "\n]";
//        }
    }
}
