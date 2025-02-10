package healeat.server.repository.ReviewRepository;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCusotm {

    Page<Review> findAllByStore(Store store, Pageable pageable);

    Page<Review> findAllByStoreAndMember_MemberDiseasesNotEmpty(Store store, Pageable pageable);

    Page<Review> findAllByStoreAndMember_Vegetarian(Store store, Vegetarian vegetarian, Pageable pageable);

    Page<Review> findAllByStoreAndMember_Diet(Store store, Diet memberDiet, Pageable pageable);

    Page<Review> findByMember(Member member,Pageable pageable);
}