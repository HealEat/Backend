package healeat.server.repository.SearchResultItemRepository;

import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface SearchResultItemRepositoryCustom {

    Page<SearchResultItem> findSortedStores(
            SearchResult searchResult,
            Set<String> placeIds,
            Float minRating,
            String sortBy,
            Pageable pageable
    );
}
