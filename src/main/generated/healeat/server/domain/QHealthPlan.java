package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHealthPlan is a Querydsl query type for HealthPlan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthPlan extends EntityPathBase<HealthPlan> {

    private static final long serialVersionUID = -950894334L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthPlan healthPlan = new QHealthPlan("healthPlan");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<healeat.server.domain.enums.Duration> duration = createEnum("duration", healeat.server.domain.enums.Duration.class);

    public final StringPath goal = createString("goal");

    public final NumberPath<Integer> goalNumber = createNumber("goalNumber", Integer.class);

    public final ListPath<HealthPlanImage, QHealthPlanImage> healthPlanImages = this.<HealthPlanImage, QHealthPlanImage>createList("healthPlanImages", HealthPlanImage.class, QHealthPlanImage.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath memo = createString("memo");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QHealthPlan(String variable) {
        this(HealthPlan.class, forVariable(variable), INITS);
    }

    public QHealthPlan(Path<? extends HealthPlan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHealthPlan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHealthPlan(PathMetadata metadata, PathInits inits) {
        this(HealthPlan.class, metadata, inits);
    }

    public QHealthPlan(Class<? extends HealthPlan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

