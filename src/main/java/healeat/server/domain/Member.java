package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.*;
import healeat.server.domain.mapping.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_answer", length = 3)
    private DietAns dietAns; // 다이어트 답변 ( ENUM : YES, NONE )

    @Enumerated(EnumType.STRING)
    @Column(name = "veget_answer")
    private Vegeterian vegetAnswer; // 채식 답변 ( ENUM )

    /**
     * 답변 리스트 -> 테이블
     */
    // (질환 보유)멤버의 건강 이슈
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "health_issue",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Enumerated(EnumType.STRING)
    private List<HealthIssueAns> healthIssueAnswers;

    // 멤버의 필요 식사 답변
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "meal_needed",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Enumerated(EnumType.STRING)
    private List<MealNeededAns> mealNeededAnswers;

    // 멤버의 필요 영양소 답변
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "nutrient_needed",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Enumerated(EnumType.STRING)
    private List<NutrientNeededAns> nutrientNeededAnswers;

    // 멤버의 피할 음식 답변
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "food_to_avoid",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<FoodToAvoidAns> foodToAvoidAnswers;

    /**
     * 연관관계 매핑
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthPlan> healthPlans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();
}
