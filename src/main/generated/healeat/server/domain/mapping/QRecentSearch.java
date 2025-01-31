package healeat.server.domain.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecentSearch is a Querydsl query type for RecentSearch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecentSearch extends EntityPathBase<RecentSearch> {

    private static final long serialVersionUID = 588600864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecentSearch recentSearch = new QRecentSearch("recentSearch");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final healeat.server.domain.QMember member;

    public final StringPath query = createString("query");

    public final EnumPath<healeat.server.domain.enums.SearchType> searchType = createEnum("searchType", healeat.server.domain.enums.SearchType.class);

    public final healeat.server.domain.QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRecentSearch(String variable) {
        this(RecentSearch.class, forVariable(variable), INITS);
    }

    public QRecentSearch(Path<? extends RecentSearch> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecentSearch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecentSearch(PathMetadata metadata, PathInits inits) {
        this(RecentSearch.class, metadata, inits);
    }

    public QRecentSearch(Class<? extends RecentSearch> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new healeat.server.domain.QMember(forProperty("member")) : null;
        this.store = inits.isInitialized("store") ? new healeat.server.domain.QStore(forProperty("store")) : null;
    }

}

