package healeat.server.domain.mapping;

import healeat.server.domain.FoodToAvoid;
import healeat.server.domain.Member;
import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.FoodToAvoidAns;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberFoodToAvoid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_to_avoid_id", nullable = false)
    private FoodToAvoid foodToAvoid;
}
