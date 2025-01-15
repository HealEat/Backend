package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DaumImageResponseDto {

    List<Document> documents;
    Meta meta;

//    @Override
//    public String toString() {
//        return "DaumImageResponse [" +
//                "documents=" + documents + ", " +
//                "meta=" + meta +
//                "\n]";
//    }

    @Getter
    public static class Document {
        String image_url;        //https://postfiles.pstatic.net/MjAxNzEyMDlfMjkg/MDAxNTEyODA4MjM2NDUz.c6ciRvtlpBjsq9SwQnm8K2VgfChNdqPGrdVloOQo4KMg._wqwlf2mIiJyC9hHlHRVoQ1YWSykZrHVMbITeqk-WTsg.JPEG.nono1114/1.jpg?type=w966
        String width;           //880,
        String height;          //583,
        String display_sitename; //"네이버블로그",
        String doc_url;          //"http://blog.naver.com/nono1114/221159368444",

//        @Override
//        public String toString() {
//            return "\nDocument [" +
//                    "\nimage_url=" + image_url + ", " +
//                    "\nwidth=" + width + ", " +
//                    "\nheight=" + height + ", " +
//                    "\ndisplay_sitename=" + display_sitename + ", " +
//                    "\ndoc_url=" + doc_url +
//                    "\n]";
//        }
    }

    @Getter
    public static class Meta {
        Integer total_count;
        Integer pageable_count;
        Boolean is_end;

//        @Override
//        public String toString() {
//            return "\nMeta [" +
//                    "\ntotalCount=" + total_count + ", " +
//                    "\npageableCount=" + pageable_count + ", " +
//                    "\nisEnd=" + is_end +
//                    "\n]";
//        }
    }
}
