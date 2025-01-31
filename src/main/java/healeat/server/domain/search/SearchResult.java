package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import healeat.server.web.dto.StoreResonseDto;
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
    private String searchId;

    private String query;   // ID 생성 관여

    private String baseX;   // ID 생성 관여

    private String baseY;   // ID 생성 관여

    private Boolean accuracy;   // ID 생성 관여

    private String keyword;

    private String selectedRegion;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> otherRegions = new ArrayList<>();

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchResultItem> items = new ArrayList<>();

    /**
     * ID 생성 메서드
     */
    public static String generateId(String query, String baseX, String baseY, Boolean accuracy) {
        // x, y 좌표를 반올림하여 비슷한 위치는 동일하게 처리
        String roundedX = roundCoordinate(baseX);
        String roundedY = roundCoordinate(baseY);

        return String.valueOf(Objects.hash(
                query != null ? query : "",
                roundedX,
                roundedY,
                accuracy));
    }

    private static final double LOCATION_THRESHOLD = 0.02; // 약 200m 정도의 차이

    private static String roundCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isBlank()) {
            return "0.0";  // 또는 다른 기본값
        }

        try {
            double value = Double.parseDouble(coordinate);
            return String.valueOf(Math.round(value / LOCATION_THRESHOLD) * LOCATION_THRESHOLD);
        } catch (NumberFormatException e) {
            return "0.0";  // 파싱 실패시 기본값
        }
    }

    /**
     * SearchResult 생성자
     */
    @Builder
    public SearchResult(String searchId, String query, String baseX, String baseY, Boolean accuracy) {
        this.searchId = searchId;
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