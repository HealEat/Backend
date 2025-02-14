package healeat.server.service;

import healeat.server.config.FoodMappingConfig;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Vegetarian;

import java.util.*;
import java.util.stream.Collectors;

public class FoodRecommendation {

    public static List<String> getFinalFoods(List<Answer> answers, Vegetarian vegetarian, Diet diet) {

        //필요한 옥식
        Set<String> recommendedFoods =
                new HashSet<>(getRecommendedFoods(answers, vegetarian, diet));
        //피해야 할 음식
        Set<String> avoidedFoods =
                new HashSet<>(getAvoidedFoods(answers, vegetarian));

        //최종 추천 음식: recommendedFoods - avoidedFoods
        recommendedFoods.removeAll(avoidedFoods);

        return new ArrayList<>(recommendedFoods);
    }

    public static List<String> getRecommendedFoods(List<Answer> answers, Vegetarian vegetarian, Diet diet) {

        // 답변 - 필요한 음식 추가
        Set<String> recommendedFoods = answers.stream()
                .map(FoodMappingConfig.ANSWER_FOOD_ASSIGNMENT::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        // 베지테리언 - 필요한 음식 추가
        if (vegetarian != Vegetarian.NONE && vegetarian != Vegetarian.FLEXI){
            recommendedFoods.addAll(FoodMappingConfig.VEGETARIAN_FOOD_ASSIGNMENT.get(vegetarian));
        }

        // 다이어트 - 필요한 음식 추가
        if (diet != Diet.NONE && diet != Diet.MAINTAIN) {
            recommendedFoods.addAll(FoodMappingConfig.DIET_FOOD_ASSIGNMENT.get(diet));
        }

        return new ArrayList<>(recommendedFoods);
    }

    public static List<String> getAvoidedFoods(List<Answer> answers, Vegetarian vegetarian) {

        // 답변 - 피해야 되는 음식 추가
        Set<String> avoidedFoods = answers.stream()
                .map(FoodMappingConfig.ANSWER_FOOD_AVOIDANCE::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());

        // 폴로 베지테리언 - 피해야 되는 음식 추가
        if (vegetarian == Vegetarian.POLO) {
            avoidedFoods.addAll(FoodMappingConfig.VEGETARIAN_FOOD_AVOIDANCE.get(vegetarian));
        }

        return new ArrayList<>(avoidedFoods);
    }
}
