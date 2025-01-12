package healeat.server.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Answer {

    // (질병 보유) 사용자 건강 이슈 답변
    WEAKENED_PHYSIC(Question.HEALTH_ISSUE),
    WEIGHT_LOSS(Question.HEALTH_ISSUE),
    INDIGESTION(Question.HEALTH_ISSUE),
    LOSS_OF_APPETITE(Question.HEALTH_ISSUE),
    PAIN(Question.HEALTH_ISSUE),
    CHRONIC(Question.HEALTH_ISSUE),

    // 필요 식사 답변
    LOW_FAT(Question.MEAL_NEEDED),
    BALANCED(Question.MEAL_NEEDED),
    LOW_SODIUM(Question.MEAL_NEEDED),
    HIGH_VEGETABLE(Question.MEAL_NEEDED),
    EASY_TO_DIGEST(Question.MEAL_NEEDED),
    ENERGY(Question.MEAL_NEEDED),

    // 필요 영양소 답변
    CARBOHYDRATES(Question.NUTRIENT_NEEDED),
    PROTEINS(Question.NUTRIENT_NEEDED),
    FATS(Question.NUTRIENT_NEEDED),
    VITAMINS(Question.NUTRIENT_NEEDED),
    MINERALS(Question.NUTRIENT_NEEDED),

    // 피할 음식 답변
    DAIRY(Question.FOOD_TO_AVOID),
    WHEAT_BASED(Question.FOOD_TO_AVOID),
    RAW_MEAT_FISH(Question.FOOD_TO_AVOID),
    MEAT(Question.FOOD_TO_AVOID),
    CAFFEINE(Question.FOOD_TO_AVOID),
    ALCOHOL(Question.FOOD_TO_AVOID),
    FIBER_BOOST_FATIGUE(Question.FOOD_TO_AVOID);

    private final Question question;

    // 질문 카테고리
    public enum Question {
        HEALTH_ISSUE,       // (질병 보유)사용자 건강 이슈
        MEAL_NEEDED,        // 필요 식사
        NUTRIENT_NEEDED,    // 필요 영양소
        FOOD_TO_AVOID       // 피할 음식
    }

    // method to get all items by question
    public List<Answer> getByQuestion(Question question) {
        return Arrays.stream(Answer.values())
                .filter(info -> ( info.getQuestion() == question))
                .toList();
    }
}
