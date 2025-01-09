package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.mapping.StoreKeyword;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 평점
     */
    private Float score; // 전체 평점

    private Float tastyScore; // 맛

    private Float cleanScore; // 청결도

    private Float freshScore; // 신선도

    private Float nutrBalanceScore; // 영양 균형

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreKeyword> storeKeywordList = new ArrayList<>();
}
