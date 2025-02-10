package healeat.server.repository.ReviewRepository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<Review> findSortedReviews(String sortBy, Pageable pageable);
}
