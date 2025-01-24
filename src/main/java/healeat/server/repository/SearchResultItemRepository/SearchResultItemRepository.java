package healeat.server.repository.SearchResultItemRepository;

import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SearchResultItemRepository extends JpaRepository<SearchResultItem, Long>, SearchResultItemRepositoryCustom {
}