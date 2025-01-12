package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Duration;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "health_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private Integer number;

    @Builder.Default
    private Integer count = 0;

    @Column(length = 50)
    private String goal;

    @Column(length = 200)
    private String memo;

    @OneToMany(mappedBy = "healthPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoImage> memoImages;

    public HealthPlan updateHealthPlan(Duration duration, Integer number, String goal) {
        return HealthPlan.builder()
                .id(this.id)
                .member(this.member)
                .duration(duration)
                .number(number)
                .count(0)
                .goal(goal)
                .memo(this.memo)
                .memoImages(this.memoImages)
                .build();
    }
}
