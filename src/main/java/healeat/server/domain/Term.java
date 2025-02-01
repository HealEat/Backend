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
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;   // 약관 제목

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String body;    // 약관 세부 내용

    @Column(nullable = false)
    private boolean isRequired; // 필수/선택 - 자동 기본값 false


    //연관관계 매핑
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms;
}
