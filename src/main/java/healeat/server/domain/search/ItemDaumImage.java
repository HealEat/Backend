package healeat.server.domain.search;

import healeat.server.domain.Store;
import healeat.server.domain.common.BaseEntity;
import healeat.server.web.dto.api_response.DaumImageResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDaumImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @Column(length = 1000, nullable = false)
    private String image_url;        //https://postfiles.pstatic.net/MjAxNzEyMDlfMjkg/MDAxNTEyODA4MjM2NDUz.c6ciRvtlpBjsq9SwQnm8K2VgfChNdqPGrdVloOQo4KMg._wqwlf2mIiJyC9hHlHRVoQ1YWSykZrHVMbITeqk-WTsg.JPEG.nono1114/1.jpg?type=w966
    private String width;           //880,
    private String height;          //583,
    private String display_sitename; //"네이버블로그",

    @Column(length = 1000, nullable = false)
    private String doc_url;          //"http://blog.naver.com/nono1114/221159368444",

    public ItemDaumImage(DaumImageResponseDto.Document document) {
        this.display_sitename = document.getDisplay_sitename();
        this.doc_url = document.getDoc_url();
        this.image_url = document.getImage_url();
        this.width = document.getWidth();
        this.height = document.getHeight();
    }

    public DaumImageResponseDto.Document toDocument() {

        return DaumImageResponseDto.Document.builder()
                .image_url(image_url)
                .width(width)
                .height(height)
                .display_sitename(display_sitename)
                .doc_url(doc_url)
                .build();
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
