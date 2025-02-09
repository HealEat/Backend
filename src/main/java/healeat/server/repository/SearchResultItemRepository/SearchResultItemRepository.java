package healeat.server.repository.SearchResultItemRepository;

import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SearchResultItemRepository extends JpaRepository<SearchResultItem, Long>, SearchResultItemRepositoryCustom {
    List<SearchResultItem> findByPlaceId(Long placeId);
}