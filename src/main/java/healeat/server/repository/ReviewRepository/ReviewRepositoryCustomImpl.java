package healeat.server.repository.ReviewRepository;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QReviewImage reviewImage = QReviewImage.reviewImage;

    @Override
    public Page<Review> findSortedReviews(String sortBy, Pageable pageable) {

//        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(sortBy, review);

        List<Review> reviews = queryFactory
                .selectFrom(review)
//                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
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

    @Override
    public Page<ReviewImage> getFirstReviewImages(Store store, Pageable pageable) {

        // 리뷰와 리뷰 이미지를 조인하여 한 번에 가져오기
        List<Tuple> results = queryFactory
                .select(review, reviewImage)
                .from(review)
                .where(review.store.eq(store))
                .leftJoin(review.reviewImageList, reviewImage)  // LEFT JOIN 활용
                .on(reviewImage.id.eq(  // 첫 번째 이미지만 가져오도록 조인 조건 추가
                        JPAExpressions.select(reviewImage.id.min())
                                .from(reviewImage)
                                .where(reviewImage.review.eq(review))
                ))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 각 리뷰에서 첫 번째 이미지 추출 (null 제거)
        List<ReviewImage> firstReviewImages = results.stream()
                .map(tuple -> tuple.get(reviewImage))  // 리뷰 이미지만 추출
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(firstReviewImages, pageable, firstReviewImages.size());
    }

//    private List<OrderSpecifier<?>> getOrderSpecifiers(String sortBy, QReview review) {
//        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//
//        OrderSpecifier<?> dynamicOrder;
//
//        switch (sortBy) {
//            case "SICK" -> dynamicOrder = review.sickScore.desc();
//            case "VEGET" -> dynamicOrder = review.vegetScore.desc();
//            case "DIET" -> dynamicOrder = review.dietScore.desc();
//            default -> dynamicOrder = review.totalScore.desc();
//        }
//
//
////        orderSpecifiers.add()
//        return null;
//    }
}
