package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.domain.mapping.RecentSearch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecentSearch> recentSearchList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarkList = new ArrayList<>();
}
