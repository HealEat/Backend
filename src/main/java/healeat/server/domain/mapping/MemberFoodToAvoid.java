package healeat.server.domain.mapping;

import healeat.server.domain.FoodToAvoid;
import healeat.server.domain.Member;
import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_food_to_avoid")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberFoodToAvoid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "food_to_avoid_id", nullable = false)
    private FoodToAvoid foodToAvoid;
}