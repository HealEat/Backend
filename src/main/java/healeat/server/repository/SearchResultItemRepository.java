package healeat.server.repository;

import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SearchResultItemRepository extends JpaRepository<SearchResultItem, Long> {

    @Query("""
            SELECT s FROM SearchResultItem s
            WHERE s.searchResult = :searchResult
            AND s.placeId IN :placeIds
            AND s.totalScore >= :minRating
            ORDER BY CASE WHEN s.reviewCount > 0 THEN 1 ELSE 0 END DESC,
                     s.distance ASC,
                     CASE :sortBy
                         WHEN 'DEFAULT' THEN s.totalScore
                         WHEN 'SICK_SCORE' THEN s.sickScore
                         WHEN 'VEGET_SCORE' THEN s.vegetScore
                         WHEN 'DIET_SCORE' THEN s.dietScore
                     END DESC
            """)
    Page<SearchResultItem> findSortedStores(
            @Param("searchResult") SearchResult searchResult,
            @Param("placeIds") Set<String> placeIds,
            @Param("minRating") Float minRating,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}