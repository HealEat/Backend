package healeat.server.domain.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberDisease is a Querydsl query type for MemberDisease
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberDisease extends EntityPathBase<MemberDisease> {

    private static final long serialVersionUID = 1933334245L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberDisease memberDisease = new QMemberDisease("memberDisease");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final healeat.server.domain.QDisease disease;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final healeat.server.domain.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberDisease(String variable) {
        this(MemberDisease.class, forVariable(variable), INITS);
    }

    public QMemberDisease(Path<? extends MemberDisease> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberDisease(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberDisease(PathMetadata metadata, PathInits inits) {
        this(MemberDisease.class, metadata, inits);
    }

    public QMemberDisease(Class<? extends MemberDisease> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.disease = inits.isInitialized("disease") ? new healeat.server.domain.QDisease(forProperty("disease")) : null;
        this.member = inits.isInitialized("member") ? new healeat.server.domain.QMember(forProperty("member")) : null;
    }

}

