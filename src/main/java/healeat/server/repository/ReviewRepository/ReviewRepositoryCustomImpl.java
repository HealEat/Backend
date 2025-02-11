package healeat.server.repository.ReviewRepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.Member;
import healeat.server.domain.QReviewImage;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.QReview;
import healeat.server.domain.mapping.Review;
import healeat.server.domain.search.QSearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;

    @Override                                      /* LATEST|DESC|ASC     SICK|VEGET|DIET */
    public Page<Review> sortAndFilterReviews(Store store, String sortBy, List<String> filters, Pageable pageable) {

        // WHERE 조건 생성 (null 방지)
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(review.store.eq(store));

        if (filters.contains("SICK")) {
            whereClause.and(review.currentDiseases.isNotEmpty());
        }
        if (filters.contains("VEGET")) {
            whereClause.and(review.currentVeget.isNotEmpty());
        }
        if (filters.contains("DIET")) {
            whereClause.and(review.currentDiet.isNotEmpty());
        }

        // 기본은 최신 순
        OrderSpecifier<?> dynamicOrder = review.createdAt.desc();
        if (sortBy.equals("DESC")) {
            dynamicOrder = review.healthScore.desc();
        }
        else if (sortBy.equals("ASC")) {
            dynamicOrder = review.healthScore.asc();
        }

        List<Review> reviews = queryFactory
                .selectFrom(review)
                .where(whereClause)
                .orderBy(dynamicOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(
                queryFactory.select(review.count())
                        .from(review)
                        .where(whereClause)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(reviews, pageable, totalCount);
    }
}
