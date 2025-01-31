package healeat.server.repository;

import healeat.server.domain.search.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SearchResultRepository extends JpaRepository<SearchResult, String> {

    Optional<SearchResult> findBySearchId(String searchId);

    // 특정 시간 이전의 검색 결과 삭제
    @Modifying
    @Query("DELETE FROM SearchResult sr WHERE sr.createdAt < :dateTime")
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}