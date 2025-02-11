package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = 577586532L;

    public static final QStore store = new QStore("store");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final StringPath addressName = createString("addressName");

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Float> cleanScore = createNumber("cleanScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> dietCount = createNumber("dietCount", Integer.class);

    public final NumberPath<Float> dietScore = createNumber("dietScore", Float.class);

    public final ListPath<String, StringPath> features = this.<String, StringPath>createList("features", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Float> freshScore = createNumber("freshScore", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoPlaceId = createNumber("kakaoPlaceId", Long.class);

    public final NumberPath<Float> nutrScore = createNumber("nutrScore", Float.class);

    public final StringPath phone = createString("phone");

    public final StringPath placeName = createString("placeName");

    public final StringPath placeUrl = createString("placeUrl");

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final ListPath<healeat.server.domain.mapping.Review, healeat.server.domain.mapping.QReview> reviews = this.<healeat.server.domain.mapping.Review, healeat.server.domain.mapping.QReview>createList("reviews", healeat.server.domain.mapping.Review.class, healeat.server.domain.mapping.QReview.class, PathInits.DIRECT2);

    public final StringPath roadAddressName = createString("roadAddressName");

    public final NumberPath<Integer> sickCount = createNumber("sickCount", Integer.class);

    public final NumberPath<Float> sickScore = createNumber("sickScore", Float.class);

    public final NumberPath<Float> tastyScore = createNumber("tastyScore", Float.class);

    public final NumberPath<Float> totalHealthScore = createNumber("totalHealthScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> vegetCount = createNumber("vegetCount", Integer.class);

    public final NumberPath<Float> vegetScore = createNumber("vegetScore", Float.class);

    public final StringPath x = createString("x");

    public final StringPath y = createString("y");

    public QStore(String variable) {
        super(Store.class, forVariable(variable));
    }

    public QStore(Path<? extends Store> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStore(PathMetadata metadata) {
        super(Store.class, metadata);
    }

}

