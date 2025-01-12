package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Duration;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
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
    private Integer goalNumber; // 목표 횟수

    @Builder.Default
    private Integer count = 0; // 목표까지 카운트

    @Column(length = 50)
    private String goal;

    @Column(length = 200)
    private String memo;

    @OneToMany(mappedBy = "healthPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoImage> memoImages;

    public HealthPlan updateHealthPlan(Duration duration, Integer goalNumber, String goal) {
        this.duration = duration;
        this.goalNumber = goalNumber;
        this.goal = goal;

        return this;
    }
}
