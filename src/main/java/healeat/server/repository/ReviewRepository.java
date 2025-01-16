package healeat.server.repository;

import healeat.server.domain.Store;
import healeat.server.domain.enums.Purpose;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByStoreAndCurrentPurposesContaining(Store store, Purpose purpose, Pageable pageable);

    Page<Review> findAllByStore(Store store, Pageable pageable);
}
