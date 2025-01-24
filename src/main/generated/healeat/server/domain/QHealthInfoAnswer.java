package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHealthInfoAnswer is a Querydsl query type for HealthInfoAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthInfoAnswer extends EntityPathBase<HealthInfoAnswer> {

    private static final long serialVersionUID = 461128997L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthInfoAnswer healthInfoAnswer = new QHealthInfoAnswer("healthInfoAnswer");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final EnumPath<healeat.server.domain.enums.Answer> answer = createEnum("answer", healeat.server.domain.enums.Answer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMemberHealQuestion memberHealQuestion;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QHealthInfoAnswer(String variable) {
        this(HealthInfoAnswer.class, forVariable(variable), INITS);
    }

    public QHealthInfoAnswer(Path<? extends HealthInfoAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHealthInfoAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHealthInfoAnswer(PathMetadata metadata, PathInits inits) {
        this(HealthInfoAnswer.class, metadata, inits);
    }

    public QHealthInfoAnswer(Class<? extends HealthInfoAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberHealQuestion = inits.isInitialized("memberHealQuestion") ? new QMemberHealQuestion(forProperty("memberHealQuestion"), inits.get("memberHealQuestion")) : null;
    }

}

