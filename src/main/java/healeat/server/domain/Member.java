package healeat.server.domain;

import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true)
    private String provider; // 소셜 로그인 제공자 ( KAKAO, NAVER, APPLE)

    @Column(nullable = true, unique = true)
    private String providerId; // 소셜 로그인 제공자로부터 받은 사용자 ID

    @Column(nullable = true)  // 애플 로그인 리프레시 토큰
    private String refreshToken;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberDisease> memberDiseases = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Vegetarian vegetarian = Vegetarian.NONE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Diet diet = Diet.NONE;

    // 건강 정보 로직 반영 결과 저장 (음식 카테고리 리스트)
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> healEatFoods = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberHealQuestion> memberHealQuestions = new ArrayList<>();  // 건강 정보 설정

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTerm> memberTerms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecentSearch> recentSearches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HealthPlan> healthPlans = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    // 애플 로그인 리프레시 토큰 업데이트 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean setVegetAndCheckChanged(Vegetarian vegetarian) {
        boolean isChanged = false;
        if (this.vegetarian != vegetarian) isChanged = true;
        this.vegetarian = vegetarian;
        return isChanged;
    }

    public boolean setDietAndCheckChanged(Diet diet) {
        boolean isChanged = false;
        if (this.diet != diet) isChanged = true;
        this.diet = diet;
        return isChanged;
    }

    public void setHealEatFoods(List<String> healEatFoods) {
        this.healEatFoods = healEatFoods;
    }

    // 프로필을 위한 업데이트 메서드
    public void updateProfile(String name, String profileImageUrl) {
        if(name != null) this.name = name;
        if(profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
