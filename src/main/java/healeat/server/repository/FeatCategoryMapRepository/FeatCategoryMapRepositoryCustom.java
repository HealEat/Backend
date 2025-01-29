package healeat.server.repository.FeatCategoryMapRepository;

import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface FeatCategoryMapRepositoryCustom {

    Set<Long> findCategoryIdsByFeatureIds(@Param("featureIds") Set<Long> featureIds);
}
