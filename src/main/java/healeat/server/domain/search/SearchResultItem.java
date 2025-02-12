package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import healeat.server.web.dto.StoreResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchResultItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_result_id")
    private SearchResult searchResult;

    private Long placeId;     // 통해서 저장된 Store 인지 확인

    private String placeName;

    private String categoryName;

    private String phone;

    private String addressName;

    private String roadAddressName;

    private String x;

    private String y;

    private String placeUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> features = new ArrayList<>();

    public StoreResponseDto.StoreInfoDto getStoreInfoDto() {

        String[] categoryWords = categoryName.split(" > ");

        return StoreResponseDto.StoreInfoDto.builder()
                .placeId(placeId)
                .placeName(placeName)
                .categoryName(categoryWords[categoryWords.length - 1])
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