package healeat.server.domain.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFeatCategoryMap is a Querydsl query type for FeatCategoryMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeatCategoryMap extends EntityPathBase<FeatCategoryMap> {

    private static final long serialVersionUID = 1109599119L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeatCategoryMap featCategoryMap = new QFeatCategoryMap("featCategoryMap");

    public final healeat.server.domain.QFoodCategory foodCategory;

    public final healeat.server.domain.QFoodFeature foodFeature;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFeatCategoryMap(String variable) {
        this(FeatCategoryMap.class, forVariable(variable), INITS);
    }

    public QFeatCategoryMap(Path<? extends FeatCategoryMap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFeatCategoryMap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFeatCategoryMap(PathMetadata metadata, PathInits inits) {
        this(FeatCategoryMap.class, metadata, inits);
    }

    public QFeatCategoryMap(Class<? extends FeatCategoryMap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.foodCategory = inits.isInitialized("foodCategory") ? new healeat.server.domain.QFoodCategory(forProperty("foodCategory")) : null;
        this.foodFeature = inits.isInitialized("foodFeature") ? new healeat.server.domain.QFoodFeature(forProperty("foodFeature")) : null;
    }

}

