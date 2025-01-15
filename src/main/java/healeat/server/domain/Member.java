package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.*;
import healeat.server.domain.mapping.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

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

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    /**
     * 건강 정보 설정
     */
/*    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "current_health_goal")
    private List<String> currentHealthGoal = new ArrayList<>();  // 현재 건강 목표

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_answer", nullable = false)
    private DietAns dietAnswer; // 다이어트 답변 ( ENUM : YES, NONE )

    @Enumerated(EnumType.STRING)
    @Column(name = "veget_answer", nullable = false)
    private Vegeterian vegetAnswer; // 채식 답변 ( ENUM )*/

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberDisease> memberDiseases = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberHealQuestion> memberHealQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPurpose> memberPurposes = new ArrayList<>();
    // 건강 정보 설정 끝


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthPlan> healthPlans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();
}
