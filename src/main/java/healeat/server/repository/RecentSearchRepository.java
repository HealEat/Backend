package healeat.server.repository;

import healeat.server.domain.mapping.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentSearchRepository extends JpaRepository <RecentSearch, Long> {
}
