package healeat.server.domain.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 5166933L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final StringPath body = createString("body");

    public final NumberPath<Float> cleanScore = createNumber("cleanScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ListPath<String, StringPath> currentPurposes = this.<String, StringPath>createList("currentPurposes", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Float> freshScore = createNumber("freshScore", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final healeat.server.domain.QMember member;

    public final NumberPath<Float> nutrScore = createNumber("nutrScore", Float.class);

    public final ListPath<healeat.server.domain.ReviewImage, healeat.server.domain.QReviewImage> reviewImageList = this.<healeat.server.domain.ReviewImage, healeat.server.domain.QReviewImage>createList("reviewImageList", healeat.server.domain.ReviewImage.class, healeat.server.domain.QReviewImage.class, PathInits.DIRECT2);

    public final healeat.server.domain.QStore store;

    public final NumberPath<Float> tastyScore = createNumber("tastyScore", Float.class);

    public final NumberPath<Float> totalScore = createNumber("totalScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new healeat.server.domain.QMember(forProperty("member")) : null;
        this.store = inits.isInitialized("store") ? new healeat.server.domain.QStore(forProperty("store")) : null;
    }

}

