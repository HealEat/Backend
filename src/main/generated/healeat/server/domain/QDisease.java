package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDisease is a Querydsl query type for Disease
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDisease extends EntityPathBase<Disease> {

    private static final long serialVersionUID = 270608703L;

    public static final QDisease disease = new QDisease("disease");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QDisease(String variable) {
        super(Disease.class, forVariable(variable));
    }

    public QDisease(Path<? extends Disease> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDisease(PathMetadata metadata) {
        super(Disease.class, metadata);
    }

}

