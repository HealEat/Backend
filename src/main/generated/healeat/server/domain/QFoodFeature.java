package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFoodFeature is a Querydsl query type for FoodFeature
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFoodFeature extends EntityPathBase<FoodFeature> {

    private static final long serialVersionUID = -616718597L;

    public static final QFoodFeature foodFeature = new QFoodFeature("foodFeature");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QFoodFeature(String variable) {
        super(FoodFeature.class, forVariable(variable));
    }

    public QFoodFeature(Path<? extends FoodFeature> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFoodFeature(PathMetadata metadata) {
        super(FoodFeature.class, metadata);
    }

}

