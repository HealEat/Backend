package healeat.server.domain;

import healeat.server.domain.enums.HealthIssueAns;
import healeat.server.domain.mapping.MemberHealthIssue;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HealthIssueAns answer; // ENUM 필드

    // 연관관계 매핑 (MemberHealthIssue 와 1:N 관계)
    @OneToMany(mappedBy = "healthIssueAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberHealthIssue> memberHealthIssues;
}