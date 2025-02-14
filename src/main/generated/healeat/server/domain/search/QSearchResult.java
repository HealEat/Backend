package healeat.server.domain.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSearchResult is a Querydsl query type for SearchResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchResult extends EntityPathBase<SearchResult> {

    private static final long serialVersionUID = -589714962L;

    public static final QSearchResult searchResult = new QSearchResult("searchResult");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final BooleanPath accuracy = createBoolean("accuracy");

    public final NumberPath<Double> avgX = createNumber("avgX", Double.class);

    public final NumberPath<Double> avgY = createNumber("avgY", Double.class);

    public final StringPath baseX = createString("baseX");

    public final StringPath baseY = createString("baseY");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<SearchResultItem, QSearchResultItem> items = this.<SearchResultItem, QSearchResultItem>createList("items", SearchResultItem.class, QSearchResultItem.class, PathInits.DIRECT2);

    public final StringPath keyword = createString("keyword");

    public final NumberPath<Double> maxMeters = createNumber("maxMeters", Double.class);

    public final ListPath<String, StringPath> otherRegions = this.<String, StringPath>createList("otherRegions", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath query = createString("query");

    public final NumberPath<Integer> radius = createNumber("radius", Integer.class);

    public final StringPath rect = createString("rect");

    public final StringPath selectedRegion = createString("selectedRegion");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSearchResult(String variable) {
        super(SearchResult.class, forVariable(variable));
    }

    public QSearchResult(Path<? extends SearchResult> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSearchResult(PathMetadata metadata) {
        super(SearchResult.class, metadata);
    }

}

