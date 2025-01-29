package healeat.server.repository.SearchResultItemRepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.QStore;
import healeat.server.domain.Store;
import healeat.server.domain.search.QSearchResultItem;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.StoreRepository;
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
public class SearchResultItemRepositoryCustomImpl implements SearchResultItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSearchResultItem searchResultItem = QSearchResultItem.searchResultItem;
    private final QStore store = QStore.store;

    @Override
    public Page<SearchResultItem> findSortedStores(
            SearchResult searchResult, List<Long> placeIds, String sortBy, Float minRating, Pageable pageable) {

        // WHERE 조건 생성 (null 방지)
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(searchResultItem.searchResult.eq(searchResult));
        if (placeIds != null && !placeIds.isEmpty()) {
            whereClause.and(searchResultItem.placeId.in(placeIds));
        }
        if (minRating != 0.0f) {
            whereClause.and(store.totalScore.goe(minRating));
        }

        List<SearchResultItem> results;

        if (sortBy.equals("NEARBY")) {
            results = queryFactory
                    .selectFrom(searchResultItem)
                    .where(whereClause)
                    .orderBy(searchResultItem.distance.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        } else {
            // 동적 정렬 생성
            List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(sortBy, searchResultItem);

            // Query 실행
            results = queryFactory
                    .selectFrom(searchResultItem)
                    .leftJoin(store).on(store.id.eq(searchResultItem.placeId))
                    .where(whereClause)
                    .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        // 전체 카운트 쿼리
        Long total = Optional.ofNullable(queryFactory
                        .select(searchResultItem.count())
                        .from(searchResultItem)
                        .leftJoin(store).on(store.id.eq(searchResultItem.placeId))
                        .where(whereClause)
                        .fetchOne())
                .orElse(0L);

        // 페이지 결과 반환
        return new PageImpl<>(results, pageable, total);
    }

    // 동적 정렬 생성 메서드
    private List<OrderSpecifier<?>> getOrderSpecifiers(String sortBy, QSearchResultItem searchResultItem) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 1. searchResultItem의 placeId와 같은 id를 가지는 Store가 존재하고, reviewCount가 0보다 큰지 확인
        orderSpecifiers.add(new CaseBuilder()
                .when(store.id.eq(searchResultItem.placeId).and(store.reviewCount.gt(0)))
                .then(1)
                .otherwise(0)
                .desc());

        // 2. 동적 정렬 기준
        OrderSpecifier<?> dynamicOrder;
        switch (sortBy) {
            case "SICK" -> dynamicOrder = store.sickScore.desc();
            case "VEGET" -> dynamicOrder = store.vegetScore.desc();
            case "DIET" -> dynamicOrder = store.dietScore.desc();
            default -> dynamicOrder = store.totalScore.desc();
        }
        orderSpecifiers.add(dynamicOrder);
        orderSpecifiers.add(searchResultItem.distance.asc());

        return orderSpecifiers;
    }
}
