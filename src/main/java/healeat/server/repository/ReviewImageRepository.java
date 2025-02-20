package healeat.server.repository;

import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    Page<ReviewImage> findAllByReview_StoreOrderByReview_CreatedAtDescCreatedAtAsc(Store store, Pageable pageable);

    Optional<ReviewImage> findFirstByReview_StoreOrderByCreatedAtDesc(Store store);
}
