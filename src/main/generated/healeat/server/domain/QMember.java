package healeat.server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 539610743L;

    public static final QMember member = new QMember("member1");

    public final healeat.server.domain.common.QBaseEntity _super = new healeat.server.domain.common.QBaseEntity(this);

    public final ListPath<healeat.server.domain.mapping.Bookmark, healeat.server.domain.mapping.QBookmark> bookmarks = this.<healeat.server.domain.mapping.Bookmark, healeat.server.domain.mapping.QBookmark>createList("bookmarks", healeat.server.domain.mapping.Bookmark.class, healeat.server.domain.mapping.QBookmark.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<healeat.server.domain.enums.Diet> diet = createEnum("diet", healeat.server.domain.enums.Diet.class);

    public final ListPath<Disease, QDisease> diseases = this.<Disease, QDisease>createList("diseases", Disease.class, QDisease.class, PathInits.DIRECT2);

    public final ListPath<String, StringPath> healEatFoods = this.<String, StringPath>createList("healEatFoods", String.class, StringPath.class, PathInits.DIRECT2);

    public final ListPath<HealthPlan, QHealthPlan> healthPlans = this.<HealthPlan, QHealthPlan>createList("healthPlans", HealthPlan.class, QHealthPlan.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberHealQuestion, QMemberHealQuestion> memberHealQuestions = this.<MemberHealQuestion, QMemberHealQuestion>createList("memberHealQuestions", MemberHealQuestion.class, QMemberHealQuestion.class, PathInits.DIRECT2);

    public final ListPath<healeat.server.domain.mapping.MemberTerm, healeat.server.domain.mapping.QMemberTerm> memberTerms = this.<healeat.server.domain.mapping.MemberTerm, healeat.server.domain.mapping.QMemberTerm>createList("memberTerms", healeat.server.domain.mapping.MemberTerm.class, healeat.server.domain.mapping.QMemberTerm.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final ListPath<healeat.server.domain.mapping.RecentSearch, healeat.server.domain.mapping.QRecentSearch> recentSearches = this.<healeat.server.domain.mapping.RecentSearch, healeat.server.domain.mapping.QRecentSearch>createList("recentSearches", healeat.server.domain.mapping.RecentSearch.class, healeat.server.domain.mapping.QRecentSearch.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<healeat.server.domain.enums.Vegetarian> vegetarian = createEnum("vegetarian", healeat.server.domain.enums.Vegetarian.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

