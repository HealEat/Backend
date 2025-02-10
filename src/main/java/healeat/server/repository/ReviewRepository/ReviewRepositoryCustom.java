package healeat.server.repository.ReviewRepository;

import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<Review> findSortedReviews(String sortBy, Pageable pageable);

    Page<ReviewImage> getFirstReviewImages(Store store, Pageable pageable);
}
