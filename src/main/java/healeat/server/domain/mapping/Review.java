package healeat.server.domain.mapping;

import healeat.server.domain.*;
import healeat.server.domain.common.BaseEntity;
import healeat.server.domain.enums.Diet;
import healeat.server.web.dto.ReviewResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> currentDiseases = new ArrayList<>();

    private String currentVeget;

    private String currentDiet;

    @Column(length = 300, nullable = false)
    private String body; // 리뷰 내용

    /**
     * 평점
     */
    private Float healthScore; // 건강 평점

    private Float tastyScore; // 평점(맛)
    private Float cleanScore; // 평점(청결도)
    private Float freshScore; // 평점(신선도)
    private Float nutrScore; // 평점(영양 균형)

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    //==비즈니스 로직==//

    @PrePersist
    public void initializeReviewAndStore() {

        // 현재 멤버의 건강 목적을 리뷰에 저장
        member.getMemberDiseases()
                .forEach(memberDisease ->
                        currentDiseases.add(memberDisease.getDisease().getName())
                );

        currentVeget = member.getVegetarian().getDescription();

        currentDiet = member.getDiet().getDescription();

        // 가게에 업데이트
        store.addScoresByReview(this);
    }

    //==연관관계 편의 메서드==//

    public void addImage(ReviewImage reviewImage) {
        this.reviewImageList.add(reviewImage);
        reviewImage.setReview(this);
    }

    //==Dto 변환==//

    public ReviewResponseDto.ReviewerInfo getReviewerInfo() {

        List<String> currentPurposes = new ArrayList<>(currentDiseases);
        currentPurposes.add(currentVeget);
        if (!currentDiet.isEmpty()) {
            currentPurposes.add("다이어트");
        }

        return ReviewResponseDto.ReviewerInfo.builder()
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .currentPurposes(currentPurposes)
                .build();
    }
}