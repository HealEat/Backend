package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rect;

    private String query;

    private String baseX;

    private String baseY;

    private Integer radius;

    private Boolean accuracy;

    /**
     * 지도 이동, 화면 뷰 위한 필드
     */
    private Double avgX;
    private Double avgY;
    private Double maxMeters;

    private String keyword;

    private String selectedRegion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> otherRegions = new ArrayList<>();

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SearchResultItem> items = new ArrayList<>();

    /**
     * Meta 데이터 입력
     */
    public void setMetaData(String keyword, String selectedRegion, List<String> otherRegions) {
        this.keyword = keyword;
        this.selectedRegion = selectedRegion;
        this.otherRegions = otherRegions;
    }

    /**
     * SearchResultItem <- Document 입력
     */
    public void addItem(SearchResultItem item) {
        this.items.add(item);
        item.setSearchResult(this);
    }

    public void setViewData(Double avgX, Double avgY, Double maxMeters) {
        this.avgX = avgX;
        this.avgY = avgY;
        this.maxMeters = maxMeters;
    }
}