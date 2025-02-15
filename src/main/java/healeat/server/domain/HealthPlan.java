package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PROGRESS;

    @Column(length = 30)
    private String goal;

    @Column(length = 200)
    private String memo;

    @OneToMany(mappedBy = "healthPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HealthPlanImage> healthPlanImages = new ArrayList<>();

    public void addImage(HealthPlanImage image) {
        healthPlanImages.add(image);
        image.setHealthPlan(this);
    }

    public HealthPlan updateHealthPlan(Duration duration, Integer goalNumber, String goal) {
        this.duration = duration;
        this.goalNumber = goalNumber;
        this.goal = goal;

        return this;
    }

    public HealthPlan updateMemo(String memo) {
        this.memo = memo;
        return this;
    }

    public HealthPlan updateStatus(String status) {
        this.status = Status.valueOf(status);
        return this;
    }
}
