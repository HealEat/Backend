package healeat.server.web.dto.api_response;

import lombok.Getter;

import java.util.List;

@Getter
public class DaumImageResponseDto {

    List<Document> documents;
    Meta meta;

    @Getter
    public static class Document {
        String image_url;        //https://postfiles.pstatic.net/MjAxNzEyMDlfMjkg/MDAxNTEyODA4MjM2NDUz.c6ciRvtlpBjsq9SwQnm8K2VgfChNdqPGrdVloOQo4KMg._wqwlf2mIiJyC9hHlHRVoQ1YWSykZrHVMbITeqk-WTsg.JPEG.nono1114/1.jpg?type=w966
        String width;           //880,
        String height;          //583,
        String display_sitename; //"네이버블로그",
        String doc_url;          //"http://blog.naver.com/nono1114/221159368444",
    }

    @Getter
    public static class Meta {
        Integer total_count;
        Integer pageable_count;
        Boolean is_end;
    }
}
