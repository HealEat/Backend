package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthPlanImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_plan_id", nullable = false)
    private HealthPlan healthPlan;

    @Column(nullable = false)
    private String filePath; // 이미지 파일 경로

    @Column(nullable = false)
    private String fileName; // 원본 파일 이름
}
