package healeat.server.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum HealthInfo {

    // NONE
    NONE,

    // Health Issues
    WEAKENED_PHYSIC(Category.HEALTH_ISSUE),
    WEIGHT_LOSS(Category.HEALTH_ISSUE),
    INDIGESTION(Category.HEALTH_ISSUE),
    LOSS_OF_APPETITE(Category.HEALTH_ISSUE),
    PAIN(Category.HEALTH_ISSUE),
    CHRONIC(Category.HEALTH_ISSUE),

    // Meal Needed
    LOW_FAT(Category.MEAL_NEEDED),
    BALANCED(Category.MEAL_NEEDED),
    LOW_SODIUM(Category.MEAL_NEEDED),
    HIGH_VEGETABLE(Category.MEAL_NEEDED),
    EASY_TO_DIGEST(Category.MEAL_NEEDED),
    ENERGY(Category.MEAL_NEEDED),

    // Nutrient Needed
    CARBOHYDRATES(Category.NUTRIENT_NEEDED),
    PROTEINS(Category.NUTRIENT_NEEDED),
    FATS(Category.NUTRIENT_NEEDED),
    VITAMINS(Category.NUTRIENT_NEEDED),
    MINERALS(Category.NUTRIENT_NEEDED),

    // Food to Avoid
    DAIRY(Category.FOOD_TO_AVOID),
    WHEAT_BASED(Category.FOOD_TO_AVOID),
    RAW_MEAT_FISH(Category.FOOD_TO_AVOID),
    MEAT(Category.FOOD_TO_AVOID),
    CAFFEINE(Category.FOOD_TO_AVOID),
    ALCOHOL(Category.FOOD_TO_AVOID),
    FIBER_BOOST_FATIGUE(Category.FOOD_TO_AVOID);

    private final Category category;

    HealthInfo(Category category) {
        this.category = category;
    }


    HealthInfo() {
        this.category = null;
    }

    public Category getCategory() {
        return category;
    }

    // Enum for categories
    public enum Category {
        HEALTH_ISSUE,
        MEAL_NEEDED,
        NUTRIENT_NEEDED,
        FOOD_TO_AVOID
    }

    // Static method to get all items by category
    public static List<HealthInfo> getByCategory(Category category) {
        return Arrays.stream(HealthInfo.values())
                .filter(info -> info.getCategory() == category)
                .toList();
    }
}
