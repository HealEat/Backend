package healeat.server.repository.ReviewRepository;

import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewRepositoryCustom {

    Page<Review> sortAndFilterReviews(Store store, String sortBy, List<String> filters, Pageable pageable);
}
