package healeat.server.repository;

import healeat.server.domain.search.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {
}