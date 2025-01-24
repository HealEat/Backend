package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberHealQuestion is a Querydsl query type for MemberHealQuestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberHealQuestion extends EntityPathBase<MemberHealQuestion> {

    private static final long serialVersionUID = 1007496709L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberHealQuestion memberHealQuestion = new QMemberHealQuestion("memberHealQuestion");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ListPath<HealthInfoAnswer, QHealthInfoAnswer> healthInfoAnswers = this.<HealthInfoAnswer, QHealthInfoAnswer>createList("healthInfoAnswers", HealthInfoAnswer.class, QHealthInfoAnswer.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final EnumPath<healeat.server.domain.enums.Question> question = createEnum("question", healeat.server.domain.enums.Question.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberHealQuestion(String variable) {
        this(MemberHealQuestion.class, forVariable(variable), INITS);
    }

    public QMemberHealQuestion(Path<? extends MemberHealQuestion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberHealQuestion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberHealQuestion(PathMetadata metadata, PathInits inits) {
        this(MemberHealQuestion.class, metadata, inits);
    }

    public QMemberHealQuestion(Class<? extends MemberHealQuestion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

