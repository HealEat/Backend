package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.DietAns;
import healeat.server.domain.enums.Vegeterian;
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
    private DietAns dietAns; // 다이어트 답변 ( ENUM : YES, NO )

    @Enumerated(EnumType.STRING)
    @Column(name = "veget_answer")
    private Vegeterian vegetAnswer; // 채식 답변 ( ENUM )

    // 연관관계 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecentSearch> recentSearchList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberMealNeeded> memberMealNeeded;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberNutrientNeeded> memberNutrientNeeded;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberFoodToAvoid> memberFoodToAvoid;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberDisease> memberDiseases;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthPlan> healthPlans;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarkList = new ArrayList<>();
}
