package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStoreThumbnail is a Querydsl query type for StoreThumbnail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreThumbnail extends EntityPathBase<StoreThumbnail> {

    private static final long serialVersionUID = 1193343976L;

    public static final QStoreThumbnail storeThumbnail = new QStoreThumbnail("storeThumbnail");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Long> placeId = createNumber("placeId", Long.class);

    public QStoreThumbnail(String variable) {
        super(StoreThumbnail.class, forVariable(variable));
    }

    public QStoreThumbnail(Path<? extends StoreThumbnail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStoreThumbnail(PathMetadata metadata) {
        super(StoreThumbnail.class, metadata);
    }

}

