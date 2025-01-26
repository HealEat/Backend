package healeat.server.domain.search;

import healeat.server.domain.common.BaseEntity;
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

    @Column
    private Boolean isInDB;

    @Column
    private String headForAPI;

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
    private Integer distance;

    // 다음 이미지 API
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> imageUrlList = new LinkedList<>();

    @JdbcTypeCode(SqlTypes.JSON)
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


    public void addReviewImgsToHead(List<String> reviewImgList) {
        this.imageUrlList.addAll(0, reviewImgList);
    }

    public void setChangableData(String headForApi, Integer distance, List<String> features) {
        this.headForAPI = headForApi;
        this.distance = distance;
        this.features = features;
    }

    @Builder
    public SearchResultItem(String placeId, Boolean isInDB, String placeName, String categoryName, String phone,
                            String addressName, String roadAddressName, String headForAPI,
                            String x, String y, String placeUrl, Integer distance,
                            List<String> imageUrlList, List<String> features,
                            Integer reviewCount, Float totalScore,
                            Float sickScore, Float vegetScore, Float dietScore) {
        this.placeId = placeId;
        this.isInDB = isInDB;
        this.headForAPI = headForAPI;
        this.placeName = placeName;
        this.categoryName = categoryName;
        this.phone = phone;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.placeUrl = placeUrl;
        this.distance = distance;
        this.imageUrlList = imageUrlList;
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