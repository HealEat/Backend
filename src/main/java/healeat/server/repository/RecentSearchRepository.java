package healeat.server.repository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.RecentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository extends JpaRepository <RecentSearch, Long> {
    Page<RecentSearch> findAllByMember(Member member, PageRequest pageRequest);

    Optional<RecentSearch> findByMemberAndId(Member member, Long id);

    Optional<RecentSearch> findByMemberAndQuery(Member member, String query);
}
