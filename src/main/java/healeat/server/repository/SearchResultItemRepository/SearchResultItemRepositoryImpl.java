package healeat.server.repository.SearchResultItemRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.search.QSearchResultItem;
import healeat.server.domain.search.SearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SearchResultItemRepositoryImpl implements SearchResultItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QSearchResultItem searchResultItem = QSearchResultItem.searchResultItem;

    @Override
    public Page<SearchResultItem> findSortedStores(String searchResultId, Set<String> placeIds, Float minRating, String sortBy, Pageable pageable) {
        return null;
    }
}
