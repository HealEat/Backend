package healeat.server.repository;

import healeat.server.domain.StoreThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreThumbnailRepository extends JpaRepository<StoreThumbnail, Long> {
    Optional<StoreThumbnail> findByPlaceId(Long placeId);
}
