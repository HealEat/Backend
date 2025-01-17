package healeat.server.domain.mapping;

import healeat.server.domain.Keyword;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.SearchType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecentSearch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 검색 기록 타입
    @Enumerated(EnumType.STRING)
    private SearchType searchType; // QUERY, STORE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    private String query;
}
