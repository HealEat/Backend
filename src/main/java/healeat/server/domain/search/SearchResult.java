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
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchResult extends BaseEntity {

    @Id
    private String searchId;

    @Column(unique = true)
    private String initId;

    private String baseX;

    private String baseY;

    private String query;

    private String selectedRegion;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> otherRegions = new ArrayList<>();

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchResultItem> items = new ArrayList<>();

    public void addItem(SearchResultItem item) {
        this.items.add(item);
        item.setSearchResult(this);
    }

    @PreRemove
    private void preRemove() {
        items.clear();
    }

    @Builder
    public SearchResult(String searchId, String initId,
                        String baseX, String baseY, String query,
                        String selectedRegion, List<String> otherRegions) {
        this.searchId = searchId;
        this.initId = initId;
        this.baseX = baseX;
        this.baseY = baseY;
        this.query = query;
        this.selectedRegion = selectedRegion;
        this.otherRegions = otherRegions;
    }
}