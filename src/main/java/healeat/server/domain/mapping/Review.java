package healeat.server.domain.mapping;

import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.common.BaseEntity;
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
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "current_health_goal")
    private List<String> currentHealthGoal = new ArrayList<>();  // 현재 건강 목표

    @Column(length = 300, nullable = false)
    private String body; // 리뷰 내용

    /**
     * 평점
     */
    private Float totalScore; // 전체 평점

    @Builder.Default
    private Float tastyScore = 1.0f; // 맛

    @Builder.Default
    private Float cleanScore = 1.0f; // 청결도

    @Builder.Default
    private Float freshScore = 1.0f; // 신선도

    @Builder.Default
    private Float nutrBalanceScore = 1.0f; // 영양 균형

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImageList = new ArrayList<>();


    @PrePersist
    public void initializeReviewAndStore() {

        // 현재 멤버의 건강 목표를 리뷰에 저장
        currentHealthGoal = member.getCurrentHealthGoal();
        // 리뷰 생성 시 전체 평점 계산
        calcTotalByAll();
        // 가게에 업데이트
        store.updateScoresByReview(this);
    }

    private void calcTotalByAll() {
        totalScore = (tastyScore + cleanScore + freshScore + nutrBalanceScore) / 4;
    }
}