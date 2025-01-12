package healeat.server.domain;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.ReviewHandler;
import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.DietAns;
import healeat.server.domain.enums.Vegeterian;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.mapping.StoreKeyword;
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
     * 평점
     */
    private Float totalScore; // 전체 평점
    private Integer reviewCount; // 전체 리뷰 수

    private Float diseaseCareScore; // 질병 관리 평점
    private Integer diseaseCareCount; // 질병 관리 리뷰 수

    private Float vegetScore; // 베지테리언 평점
    private Integer vegetCount; // 베지테리언 리뷰 수

    private Float dietScore; // 다이어터 평점
    private Integer dietCount; // 다이어터 리뷰 수

    private Float tastyScore; // 평점(맛)
    private Float cleanScore; // 평점(청결도)
    private Float freshScore; // 평점(신선도)
    private Float nutrBalanceScore; // 평점(영양 균형)


    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreKeyword> storeKeywords = new ArrayList<>();


    //==비즈니스 로직==//

    /**
     * 새로운 리뷰에 의한
     * 가게 평점 업데이트
     */
    private float updateScore(float currentScore, float newScore) {
        return (currentScore * reviewCount + newScore) / (reviewCount + 1);
    }

    public void updateScoresByReview(Review newReview) {
        if (newReview == null) {
            throw new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND);
        }

        tastyScore = updateScore(tastyScore, newReview.getTastyScore());
        cleanScore = updateScore(cleanScore, newReview.getCleanScore());
        freshScore = updateScore(freshScore, newReview.getFreshScore());
        nutrBalanceScore = updateScore(nutrBalanceScore, newReview.getNutrBalanceScore());

        reviewCount++; // 리뷰 수 증가

        // 건강 목표 관련 로직 추가 해야

        calcTotalByAll();
    }

    private void calcTotalByAll() {
        totalScore = (tastyScore + cleanScore + freshScore + nutrBalanceScore) / 4;
    }
}
