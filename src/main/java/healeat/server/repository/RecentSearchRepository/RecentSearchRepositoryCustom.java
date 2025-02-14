package healeat.server.repository.RecentSearchRepository;

import healeat.server.domain.mapping.RecentSearch;

import java.util.List;

public interface RecentSearchRepositoryCustom {
    List<RecentSearch> findTop20RecentSearchesByMember(Long memberId);
}
