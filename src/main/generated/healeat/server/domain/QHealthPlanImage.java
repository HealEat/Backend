package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHealthPlanImage is a Querydsl query type for HealthPlanImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthPlanImage extends EntityPathBase<HealthPlanImage> {

    private static final long serialVersionUID = 616008057L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthPlanImage healthPlanImage = new QHealthPlanImage("healthPlanImage");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QHealthPlan healthPlan;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QHealthPlanImage(String variable) {
        this(HealthPlanImage.class, forVariable(variable), INITS);
    }

    public QHealthPlanImage(Path<? extends HealthPlanImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHealthPlanImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHealthPlanImage(PathMetadata metadata, PathInits inits) {
        this(HealthPlanImage.class, metadata, inits);
    }

    public QHealthPlanImage(Class<? extends HealthPlanImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.healthPlan = inits.isInitialized("healthPlan") ? new QHealthPlan(forProperty("healthPlan"), inits.get("healthPlan")) : null;
    }

}

