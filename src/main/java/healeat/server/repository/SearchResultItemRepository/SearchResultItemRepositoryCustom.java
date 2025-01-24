package healeat.server.repository.SearchResultItemRepository;

import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SearchResultItemRepositoryCustom {

    Page<SearchResultItem> findSortedStores(
            @Param("searchResultId") String searchResultId,
            @Param("placeIds") Set<String> placeIds,
            @Param("minRating") Float minRating,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}
