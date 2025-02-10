package healeat.server.repository.ReviewRepository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.QReview;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.search.QSearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;

    @Override
    public Page<Review> findSortedReviews(String sortBy, Pageable pageable) {

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(sortBy, review);

        List<Review> reviews = queryFactory
                .selectFrom(review)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(queryFactory
                .select(review.count())
                .from(review)
                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(reviews, pageable, totalCount);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(String sortBy, QReview review) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        OrderSpecifier<?> dynamicOrder;

        switch (sortBy) {
            case "SICK" -> dynamicOrder = review.sickScore.desc();
            case "VEGET" -> dynamicOrder = review.vegetScore.desc();
            case "DIET" -> dynamicOrder = review.dietScore.desc();
            default -> dynamicOrder = review.totalScore.desc();
        }


//        orderSpecifiers.add()
        return null;
    }
}
