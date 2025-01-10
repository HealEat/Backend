package healeat.server.domain.mapping;

import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

    @Column(nullable = false, length = 300)
    private String body; // 리뷰 내용


    /**
     * 평점
     */
    private Float score; // 전체 평점

    private Float tastyScore; // 맛

    private Float cleanScore; // 청결도

    private Float freshScore; // 신선도

    private Float nutrBalanceScore; // 영양 균형

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImageList = new ArrayList<>();
}