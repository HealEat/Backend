package healeat.server.domain.mapping;

import healeat.server.domain.MealNeeded;
import healeat.server.domain.Member;
import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_meal_needed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberMealNeeded extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "meal_needed_id", nullable = false)
    private MealNeeded mealNeeded;
}
