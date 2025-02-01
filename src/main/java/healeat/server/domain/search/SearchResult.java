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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String query;

    private String baseX;

    private String baseY;

    private Boolean accuracy;

    private String keyword;

    private String selectedRegion;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> otherRegions = new ArrayList<>();

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchResultItem> items = new ArrayList<>();

    /**
     * SearchResult 생성자
     */
    @Builder
    public SearchResult(String query, String baseX, String baseY, Boolean accuracy) {
        this.query = query;
        this.baseX = baseX;
        this.baseY = baseY;
        this.accuracy = accuracy;
    }

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
}