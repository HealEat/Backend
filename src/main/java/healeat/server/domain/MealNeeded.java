package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.MealNeededAns;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_needed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MealNeeded extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealNeededAns answer;
}
