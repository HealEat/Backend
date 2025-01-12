package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.mapping.MemberTerm;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false)
    private String body;

    //연관관계 매핑
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms;
}
