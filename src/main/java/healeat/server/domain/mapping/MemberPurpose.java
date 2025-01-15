package healeat.server.domain.mapping;

import healeat.server.domain.Member;
import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Purpose;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberPurpose extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private Purpose purpose;

    @OneToMany(mappedBy = "memberPurpose", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurposeAnswer> purposeAnswers = new ArrayList<>();
}
