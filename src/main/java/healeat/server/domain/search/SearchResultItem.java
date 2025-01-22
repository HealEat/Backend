package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
                @Index(name = "idx_search_result_review_count", columnList = "search_result_id, reviewCount"),
                @Index(name = "idx_search_result_total_score", columnList = "search_result_id, totalScore"),
                @Index(name = "idx_search_result_distance", columnList = "search_result_id, distance")
        })
public class SearchResultItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_result_id")
    private SearchResult searchResult;

    @Column(nullable = false)
    private String placeId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String categoryName;

    @Column
    private String phone;

    @Column
    private String addressName;

    @Column
    private String roadAddressName;

    @Column(nullable = false)
    private String x;

    @Column(nullable = false)
    private String y;

    @Column
    private String placeUrl;

    @Column
    private String distance;

    @ElementCollection
    private List<String> features = new ArrayList<>();

    @Column
    private Integer reviewCount;

    @Column
    private Float totalScore;

    @Column
    private Float sickScore;

    @Column
    private Float vegetScore;

    @Column
    private Float dietScore;

    @Builder
    public SearchResultItem(String placeId, String placeName, String categoryName,
                            String phone, String addressName, String roadAddressName,
                            String x, String y, String placeUrl, String distance,
                            List<String> features, Integer reviewCount, Float totalScore,
                            Float sickScore, Float vegetScore, Float dietScore) {
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
        this.features = features;
        this.reviewCount = reviewCount;
        this.totalScore = totalScore;
        this.sickScore = sickScore;
        this.vegetScore = vegetScore;
        this.dietScore = dietScore;
    }

    void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
}