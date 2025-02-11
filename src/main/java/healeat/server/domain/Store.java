package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.StoreResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends BaseEntity {

    /**
     * 1. DB에 없는데 조회
     * 2. DB에 없는데 북마크
     *  -> Store 데이터 저장
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long kakaoPlaceId;  // 카카오 API의 Place ID를 저장

    /**
     * 가게 정보 (from Kakao Local API)
     *  : 저장 trigger
     *      - 가게에 대해 최초로 리뷰 or 리뷰 없는데 북마크
     */
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

    /**
     * 평점
     */

    private Float totalScore; // 전체 평점
    private Integer reviewCount; // 전체 리뷰 수

    private Float sickScore; // 환자 평점
    private Integer sickCount; // 환자 리뷰 수

    private Float vegetScore; // 베지테리언 평점
    private Integer vegetCount; // 베지테리언 리뷰 수

    private Float dietScore; // 다이어터 평점
    private Integer dietCount; // 다이어터 리뷰 수

    private Float tastyScore; // 평점(맛)
    private Float cleanScore; // 평점(청결도)
    private Float freshScore; // 평점(신선도)
    private Float nutrScore; // 평점(영양 균형)

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @PrePersist
    public void initializeStore() {
        totalScore = 0.0f;
        reviewCount = 0;
        sickScore = 0.0f;
        sickCount = 0;
        vegetScore = 0.0f;
        vegetCount = 0;
        dietScore = 0.0f;
        dietCount = 0;

        tastyScore = 0.0f;
        cleanScore = 0.0f;
        freshScore = 0.0f;
        nutrScore = 0.0f;
    }

    //==비즈니스 로직==//

    /**
     * 새로운 리뷰에 의한
     * 가게 평점 업데이트
     */
    private float addReviewScore(float currentScore, float newScore) {
        return (currentScore * reviewCount + newScore) / (reviewCount + 1);
    }

    public void addScoresByReview(Review newReview) {

        Float newReviewTotal = newReview.getTotalScore();
        if (!newReview.getCurrentDiseases().isEmpty()) {
            sickScore = (
                    sickScore * sickCount + newReviewTotal) / (sickCount + 1);
            sickCount++;
        }
        if (!newReview.getCurrentVeget().isEmpty()) {
            vegetScore = (
                    vegetScore * vegetCount + newReviewTotal) / (vegetCount + 1);
            vegetCount++;
        }
        if (!newReview.getCurrentDiet().isEmpty()) {
            dietScore = (
                    dietScore * dietCount + newReviewTotal) / (dietCount + 1);
            dietCount++;
        }

        tastyScore = addReviewScore(tastyScore, newReview.getTastyScore());
        cleanScore = addReviewScore(cleanScore, newReview.getCleanScore());
        freshScore = addReviewScore(freshScore, newReview.getFreshScore());
        nutrScore = addReviewScore(nutrScore, newReview.getNutrScore());

        reviewCount++; // 리뷰 수 증가

        calcTotalByAll();

        // 연관 관계
        reviews.add(newReview);
    }

    /**
     * 리뷰 삭제에 의한
     * 가게 평점 업데이트
     */
    private float subtractReviewScore(float currentScore, float newScore) {
        return (currentScore * reviewCount - newScore) / (reviewCount - 1);
    }

    public void deleteReview(Review review) {

        Float reviewTotal = review.getTotalScore();
        if (!review.getCurrentDiseases().isEmpty()) {
            sickScore = (
                    sickScore * sickCount - reviewTotal) / (sickCount - 1);
            sickCount--;
        }
        if (!review.getCurrentVeget().isEmpty()) {
            vegetScore = (
                    vegetScore * vegetCount - reviewTotal) / (vegetCount - 1);
            vegetCount--;
        }
        if (!review.getCurrentDiet().isEmpty()) {
            dietScore = (
                    dietScore * dietCount - reviewTotal) / (dietCount - 1);
            dietCount--;
        }

        tastyScore = subtractReviewScore(tastyScore, review.getTastyScore());
        cleanScore = subtractReviewScore(cleanScore, review.getCleanScore());
        freshScore = subtractReviewScore(freshScore, review.getFreshScore());
        nutrScore = subtractReviewScore(nutrScore, review.getNutrScore());

        reviewCount--; // 리뷰 수 감소

        calcTotalByAll();

        // 연관 관계
        reviews.remove(review);
    }

    private void calcTotalByAll() {
        totalScore = (tastyScore + cleanScore + freshScore + nutrScore) / 4;
    }

    //==Dto 변환==//

    public StoreResponseDto.TotalStatDto getTotalStatDto() {
        return StoreResponseDto.TotalStatDto.builder()
                .tastyScore(tastyScore)
                .cleanScore(cleanScore)
                .freshScore(freshScore)
                .nutrScore(nutrScore)
                .build();
    }

    public StoreResponseDto.IsInDBDto getIsInDBDto() {
        return StoreResponseDto.IsInDBDto.builder()
                .totalScore(totalScore)
                .reviewCount(reviewCount)
                .sickScore(sickScore)
                .sickCount(sickCount)
                .vegetScore(vegetScore)
                .vegetCount(vegetCount)
                .dietScore(dietScore)
                .dietCount(dietCount)
                .build();
    }

    public StoreResponseDto.StoreInfoDto getStoreInfoDto() {

        String[] categoryWords = categoryName.split(" > ");

        return StoreResponseDto.StoreInfoDto.builder()
                .placeId(kakaoPlaceId)
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

    public StoreResponseDto.StoreHomeDto getStoreHomeDto() {

        return StoreResponseDto.StoreHomeDto.builder()
                .storeId(id)
                .createdAt(getCreatedAt())
                .storeInfoDto(getStoreInfoDto())
                .totalStatDto(getTotalStatDto())
                .build();
    }
}
