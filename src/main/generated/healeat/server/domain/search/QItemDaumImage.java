package healeat.server.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItemDaumImage is a Querydsl query type for ItemDaumImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItemDaumImage extends EntityPathBase<ItemDaumImage> {

    private static final long serialVersionUID = -366664758L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItemDaumImage itemDaumImage = new QItemDaumImage("itemDaumImage");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath display_sitename = createString("display_sitename");

    public final StringPath doc_url = createString("doc_url");

    public final StringPath height = createString("height");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image_url = createString("image_url");

    public final healeat.server.domain.QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath width = createString("width");

    public QItemDaumImage(String variable) {
        this(ItemDaumImage.class, forVariable(variable), INITS);
    }

    public QItemDaumImage(Path<? extends ItemDaumImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItemDaumImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItemDaumImage(PathMetadata metadata, PathInits inits) {
        this(ItemDaumImage.class, metadata, inits);
    }

    public QItemDaumImage(Class<? extends ItemDaumImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new healeat.server.domain.QStore(forProperty("store")) : null;
    }

}

