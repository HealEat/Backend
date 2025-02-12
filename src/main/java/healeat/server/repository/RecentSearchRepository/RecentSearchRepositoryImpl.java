package healeat.server.repository.RecentSearchRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.mapping.RecentSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static healeat.server.domain.mapping.QRecentSearch.recentSearch;

@Repository
@RequiredArgsConstructor
public class RecentSearchRepositoryImpl implements RecentSearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecentSearch> findTop20RecentSearchesByMember(Long memberId) {
        return queryFactory
                .selectFrom(recentSearch)
                .where(recentSearch.member.id.eq(memberId)) // ✅ 특정 회원 검색
                .orderBy(recentSearch.updatedAt.desc()) // 최신순 정렬
                .limit(20) // ✅ 20개만 가져오기
                .fetch();
    }
}
