package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberHealthInfoQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Question question;

    @OneToMany(mappedBy = "healthInfoQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthInfoAnswer> answers = new ArrayList<>();
}
