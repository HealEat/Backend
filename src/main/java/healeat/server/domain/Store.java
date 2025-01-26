package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.Review;
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
     * 최초 리뷰가 발생할 때
     * DB에 Store 데이터 저장
     */
    @Id
    // 카카오 API의 가게 ID 값을 직접 할당
    private Long id;

    /**
     * 가게 정보 (from Kakao Local API)
     *  저장 trigger - 가게에 대한 최초 리뷰, 최초 북마크
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
    private List<String> daumImgUrlList = new ArrayList<>();

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

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    //==비즈니스 로직==//

    /**
     * 새로운 리뷰에 의한
     * 가게 평점 업데이트
     */
    private float updateScore(float currentScore, float newScore) {
        return (currentScore * reviewCount + newScore) / (reviewCount + 1);
    }

    public void updateScoresByReview(Review newReview) {

        Member member = newReview.getMember();
        Float newReviewTotal = newReview.getTotalScore();
        if (!member.getMemberDiseases().isEmpty()) {
            sickScore = (
                    sickScore * sickCount + newReviewTotal) / (sickCount + 1);
            sickCount++;
        }
        if (member.getVegetarian() != Vegetarian.NONE) {
            vegetScore = (
                    vegetScore * vegetCount + newReviewTotal) / (vegetCount + 1);
            vegetCount++;
        }
        if (member.getDiet() != Diet.NONE) {
            dietScore = (
                    dietScore * dietCount + newReviewTotal) / (dietCount + 1);
            dietCount++;
        }

        tastyScore = updateScore(tastyScore, newReview.getTastyScore());
        cleanScore = updateScore(cleanScore, newReview.getCleanScore());
        freshScore = updateScore(freshScore, newReview.getFreshScore());
        nutrScore = updateScore(nutrScore, newReview.getNutrScore());

        reviewCount++; // 리뷰 수 증가

        calcTotalByAll();
    }

    private void calcTotalByAll() {
        totalScore = (tastyScore + cleanScore + freshScore + nutrScore) / 4;
    }
}
