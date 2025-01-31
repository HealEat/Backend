package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import healeat.server.web.dto.StoreResonseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchResultItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_result_id")
    private SearchResult searchResult;

    private Integer distance;

    private Long placeId;     // 통해서 저장된 Store 인지 확인

    private String placeName;

    private String categoryName;

    private String phone;

    private String addressName;

    private String roadAddressName;

    private String x;

    private String y;

    private String placeUrl;

    // 다음 이미지 API
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> daumImageUrlList = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> features = new ArrayList<>();


    @Builder
    public SearchResultItem(Long placeId, String placeName, String categoryName,
                            String phone, String addressName, String roadAddressName,
                            String x, String y, String placeUrl, Integer distance,
                            List<String> daumImageUrlList, List<String> features) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.categoryName = categoryName;
        this.phone = phone;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.placeUrl = placeUrl;
        this.distance = distance;
        this.daumImageUrlList = daumImageUrlList;
        this.features = features;
    }

    public StoreResonseDto.StoreInfoDto getStoreInfoDto() {
        return StoreResonseDto.StoreInfoDto.builder()
                .distance(distance)
                .placeId(placeId)
                .placeName(placeName)
                .categoryName(categoryName)
                .phone(phone)
                .addressName(addressName)
                .roadAddressName(roadAddressName)
                .x(x)
                .y(y)
                .placeUrl(placeUrl)
                .features(features)
                .build();
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
}