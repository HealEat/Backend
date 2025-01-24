package healeat.server.repository;

import healeat.server.domain.search.SearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SearchResultItemRepository extends JpaRepository<SearchResultItem, Long> {

    // 동적 정렬을 위한 네이티브 SQL
    @Query(value = """
    SELECT * FROM search_result_item s
    WHERE s.search_result_id = :searchResultId
      AND s.place_id IN :placeIds
      AND s.total_score >= :minRating
    ORDER BY 
        IF(:sortBy = 'DEFAULT', s.total_score, 
           IF(:sortBy = 'SICK', s.sick_score, 
           IF(:sortBy = 'VEGET', s.veget_score, 
           IF(:sortBy = 'DIET', s.diet_score, s.total_score)))) DESC,
        s.distance ASC
    """, nativeQuery = true)
    Page<SearchResultItem> findSortedStores(
            @Param("searchResultId") String searchResultId,
            @Param("placeIds") Set<String> placeIds,
            @Param("minRating") Float minRating,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}