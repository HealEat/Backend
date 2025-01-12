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

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_answer", nullable = false)
    private DietAns dietAnswer; // 다이어트 답변 ( ENUM : YES, NONE )

    @Enumerated(EnumType.STRING)
    @Column(name = "veget_answer", nullable = false)
    private Vegeterian vegetAnswer; // 채식 답변 ( ENUM )

    /**
     * 멤버의 건강 정보 필드
     */
    // toString() 출력 test 결과
    // {MEAL_NEEDED=[NONE], NUTRIENT_NEEDED=[NONE], FOOD_TO_AVOID=[NONE], HEALTH_ISSUE=[NONE]}
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "health_info_by_category")
    private Map<HealthInfo.Category, Set<HealthInfo>> healthInfoByCategories;

    // Set 중복 허용하지 않음
    @PrePersist
    public void prePersist() {
        healthInfoByCategories = initializeHealthInfoMap();
    }

    private static Map<HealthInfo.Category, Set<HealthInfo>> initializeHealthInfoMap() {
        Map<HealthInfo.Category, Set<HealthInfo>> map = new HashMap<>();

        Set<HealthInfo> defaultHealthInfo = EnumSet.of(HealthInfo.NONE);

        map.put(HealthInfo.Category.HEALTH_ISSUE, defaultHealthInfo);
        map.put(HealthInfo.Category.MEAL_NEEDED, defaultHealthInfo);
        map.put(HealthInfo.Category.NUTRIENT_NEEDED, defaultHealthInfo);
        map.put(HealthInfo.Category.FOOD_TO_AVOID, defaultHealthInfo);

        return map;
    }
    // 멤버의 건강 정보 필드 끝

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTerm> memberTerms;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthPlan> healthPlans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();
}
