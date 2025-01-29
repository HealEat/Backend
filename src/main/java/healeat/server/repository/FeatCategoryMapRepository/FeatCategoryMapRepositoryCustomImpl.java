package healeat.server.repository.FeatCategoryMapRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import healeat.server.domain.mapping.QFeatCategoryMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeatCategoryMapRepositoryCustomImpl implements FeatCategoryMapRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFeatCategoryMap featCategoryMap = QFeatCategoryMap.featCategoryMap;

    @Override
    public Set<Long> findCategoryIdsByFeatureIds(Set<Long> featureIds) {
        return new HashSet<>(queryFactory.select(featCategoryMap.foodCategory.id)
                .from(featCategoryMap)
                .where(featCategoryMap.foodFeature.id.in(featureIds))
                .fetch());
    }
}
