package healeat.server.domain.enums;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.HealthInfoHandler;

import java.util.Arrays;

public enum Answer {

    // (질병 보유) 사용자 건강 이슈 답변
    WEAKENED_PHYSIC("체력/신체 기능 감소", Question.HEALTH_ISSUE),
    WEIGHT_LOSS("체중 감소", Question.HEALTH_ISSUE),
    INDIGESTION("소화 불량", Question.HEALTH_ISSUE),
    LOSS_OF_APPETITE("식욕 부진", Question.HEALTH_ISSUE),
    PAIN("통증", Question.HEALTH_ISSUE),
    CHRONIC_FATIGUE("만성 피로", Question.HEALTH_ISSUE),

    // 필요 식사 답변
    LOW_FAT("기름기 적은 식사", Question.MEAL_NEEDED),
    BALANCED("영양소가 고르게 포함된 식사", Question.MEAL_NEEDED),
    LOW_SODIUM("싱겁게 먹는 식사", Question.MEAL_NEEDED),
    HIGH_VEGETABLE("채소가 많은 식사", Question.MEAL_NEEDED),
    EASY_TO_DIGEST("소화가 잘 되는 식사", Question.MEAL_NEEDED), // INDIGESTION 과 매핑 동일
    ENERGY("에너지를 보충해주는 식사", Question.MEAL_NEEDED), // WEAKEND_PHYSIC 과 매핑 동일

    // 필요 영양소 답변
    CARBOHYDRATES("탄수화물", Question.NUTRIENT_NEEDED),
    PROTEINS("단백질", Question.NUTRIENT_NEEDED),
    FATS("지방", Question.NUTRIENT_NEEDED),
    VITAMINS("비타민", Question.NUTRIENT_NEEDED),
    MINERALS("무기질", Question.NUTRIENT_NEEDED),
    FIBRE("섬유소", Question.NUTRIENT_NEEDED),

    // 피할 음식 답변
    MEAT("육류", Question.FOOD_TO_AVOID),
    WHEAT_BASED("밀가루 음식", Question.FOOD_TO_AVOID),
    RAW_MEAT_FISH("날 고기/생선(회)", Question.FOOD_TO_AVOID),
    FISH("생선류", Question.FOOD_TO_AVOID),
    MOLLUSK("연체류", Question.FOOD_TO_AVOID),
    SPICY("매운 음식", Question.FOOD_TO_AVOID);

    private final String description;

    private final Question question;

    Answer(String description, Question question) {
        this.description = description;
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public String getDescription() {
        return description;
    }

    public static Answer getByDescription(String description) {
        return Arrays.stream(Answer.values())
                .filter(a -> a.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new HealthInfoHandler(ErrorStatus.ANSWER_NOT_FOUND));
    }
}