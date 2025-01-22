package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import healeat.server.web.dto.StoreResonseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.internal.util.MutableInteger;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchResult extends BaseEntity {

    @Id
    private String searchId;

    private String baseX;

    private String baseY;

    private String query;

    private String selectedRegion;

    @ElementCollection
    private List<String> otherRegions = new ArrayList<>();

    @Column
    private Integer apiCallCount;

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL)
    private List<SearchResultItem> items = new ArrayList<>();

    @Builder
    public SearchResult(String searchId, String baseX, String baseY, String query,
                        String selectedRegion, List<String> otherRegions, Integer apiCallCount) {
        this.searchId = searchId;
        this.baseX = baseX;
        this.baseY = baseY;
        this.query = query;
        this.selectedRegion = selectedRegion;
        this.otherRegions = otherRegions;
        this.apiCallCount = apiCallCount;
    }

    public void addItem(SearchResultItem item) {
        this.items.add(item);
        item.setSearchResult(this);
    }

    public StoreResonseDto.SearchInfo toSearchInfo(MutableInteger apiCallCounter) {
        return StoreResonseDto.SearchInfo.builder()
                .baseX(baseX)
                .baseY(baseY)
                .query(query)
                .selectedRegion(selectedRegion)
                .otherRegions(otherRegions)
                .apiCallCount(apiCallCounter.get())
                .build();
    }
}