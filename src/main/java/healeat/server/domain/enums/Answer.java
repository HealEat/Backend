package healeat.server.domain.enums;

public enum Answer {
    // (질병 보유) 사용자 건강 이슈 답변
    WEAKENED_PHYSIC,
    WEIGHT_LOSS,
    INDIGESTION,
    LOSS_OF_APPETITE,
    PAIN,
    CHRONIC_FATIGUE,

    // 필요 식사 답변
    LOW_FAT,
    BALANCED,
    LOW_SODIUM,
    HIGH_VEGETABLE,
    EASY_TO_DIGEST, // INDIGESTION 과 매핑 동일
    ENERGY, // WEAKEND_PHYSIC 과 매핑 동일

    // 필요 영양소 답변
    CARBOHYDRATES,
    PROTEINS,
    FATS,
    VITAMINS,
    MINERALS,
    FIBRE,

    // 피할 음식 답변
    MEAT,
    WHEAT_BASED,
    RAW_MEAT_FISH,
    FISH,
    MOLLUSK,
    SPICY
}
