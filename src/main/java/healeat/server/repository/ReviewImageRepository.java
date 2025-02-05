package healeat.server.repository;

import healeat.server.domain.ReviewImage;
import healeat.server.domain.mapping.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findAllByReview(Review review);
}
