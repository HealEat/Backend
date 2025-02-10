package healeat.server.repository.ReviewRepository;

import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    Page<Review> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
}