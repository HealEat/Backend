package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentSearchRepository extends JpaRepository <RecentSearch, Long> {
    List<RecentSearch> findByMember(Member member);
}
