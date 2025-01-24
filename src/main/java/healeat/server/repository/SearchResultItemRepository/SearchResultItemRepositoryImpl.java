package healeat.server.repository.SearchResultItemRepository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.search.QSearchResultItem;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SearchResultItemRepositoryImpl implements SearchResultItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSearchResultItem searchResultItem = QSearchResultItem.searchResultItem;

    @Override
    public Page<SearchResultItem> findSortedStores(
            SearchResult searchResult, Set<String> placeIds, Float minRating, String sortBy, Pageable pageable) {

        // WHERE 조건 생성
        BooleanExpression whereClause = searchResultItem.searchResult.eq(searchResult)
                .and(placeIds != null && !placeIds.isEmpty() ? searchResultItem.placeId.in(placeIds) : null)
                .and(searchResultItem.totalScore.goe(minRating));

        // 동적 정렬 생성
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(sortBy, searchResultItem);

        // Query 실행
        List<SearchResultItem> results = queryFactory
                .selectFrom(searchResultItem)
                .where(whereClause)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        Long total = Optional.ofNullable(queryFactory
                        .select(searchResultItem.count())
                        .from(searchResultItem)
                        .where(whereClause)
                        .fetchOne())
                .orElse(0L);

        // 페이지 결과 반환
        return new PageImpl<>(results, pageable, total);
    }

    // 동적 정렬 생성 메서드
    private List<OrderSpecifier<?>> getOrderSpecifiers(String sortBy, QSearchResultItem searchResultItem) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 1. 리뷰 카운트가 0보다 큰 항목이 먼저
        orderSpecifiers.add(new CaseBuilder()
                .when(searchResultItem.reviewCount.gt(0))
                .then(1)
                .otherwise(0)
                .desc());

        // 2. 동적 정렬 기준
        OrderSpecifier<?> dynamicOrder;
        switch (sortBy) {
            case "SICK" -> dynamicOrder = searchResultItem.sickScore.desc();
            case "VEGET" -> dynamicOrder = searchResultItem.vegetScore.desc();
            case "DIET" -> dynamicOrder = searchResultItem.dietScore.desc();
            default -> dynamicOrder = searchResultItem.totalScore.desc();
        }
        orderSpecifiers.add(dynamicOrder);

        // 3. 거리 기준 정렬
        orderSpecifiers.add(searchResultItem.distance.asc());

        return orderSpecifiers;
    }
}
