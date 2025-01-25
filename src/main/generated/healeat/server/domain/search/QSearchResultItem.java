package healeat.server.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSearchResultItem is a Querydsl query type for SearchResultItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchResultItem extends EntityPathBase<SearchResultItem> {

    private static final long serialVersionUID = 588902945L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSearchResultItem searchResultItem = new QSearchResultItem("searchResultItem");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final StringPath addressName = createString("addressName");

    public final StringPath categoryName = createString("categoryName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Float> dietScore = createNumber("dietScore", Float.class);

    public final NumberPath<Integer> distance = createNumber("distance", Integer.class);

    public final ListPath<String, StringPath> features = this.<String, StringPath>createList("features", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath headForAPI = createString("headForAPI");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> imageUrlList = this.<String, StringPath>createList("imageUrlList", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath phone = createString("phone");

    public final StringPath placeId = createString("placeId");

    public final StringPath placeName = createString("placeName");

    public final StringPath placeUrl = createString("placeUrl");

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final StringPath roadAddressName = createString("roadAddressName");

    public final QSearchResult searchResult;

    public final NumberPath<Float> sickScore = createNumber("sickScore", Float.class);

    public final NumberPath<Float> totalScore = createNumber("totalScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Float> vegetScore = createNumber("vegetScore", Float.class);

    public final StringPath x = createString("x");

    public final StringPath y = createString("y");

    public QSearchResultItem(String variable) {
        this(SearchResultItem.class, forVariable(variable), INITS);
    }

    public QSearchResultItem(Path<? extends SearchResultItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSearchResultItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSearchResultItem(PathMetadata metadata, PathInits inits) {
        this(SearchResultItem.class, metadata, inits);
    }

    public QSearchResultItem(Class<? extends SearchResultItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.searchResult = inits.isInitialized("searchResult") ? new QSearchResult(forProperty("searchResult")) : null;
    }

}

