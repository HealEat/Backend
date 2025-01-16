package healeat.server.service;

import healeat.server.config.FoodMappingConfig;
import healeat.server.domain.enums.Answer;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.Vegetarian;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodRecommendationService {

    public static List<String> getRecommendedFoods (Set<Answer> answers, Set<Vegetarian> vegetarians, Set<Diet> diets) {
        Set<String> recommendedFoods = new HashSet<>();

        // 답변 - 필요한 음식 추가
        for (Answer answer : answers) {
            List<String> answerFoods = FoodMappingConfig.ANSWER_FOOD_ASSIGNMENT.get(answer);
            if (answerFoods != null) {
                recommendedFoods.addAll(answerFoods);
            }
        }
        // 베지테리언 - 필요한 음식 추가
        for (Vegetarian vegetarian : vegetarians) {
            List<String> vegetarianFoods = FoodMappingConfig.VEGETARIAN_FOOD_ASSIGNMENT.get(vegetarian);
            if (vegetarianFoods != null) {
                recommendedFoods.addAll(vegetarianFoods);
            }
        }
        // 다이어트 - 필요한 음식 추가
        for (Diet diet : diets) {
            List<String> dietFoods = FoodMappingConfig.DIET_FOOD_ASSIGNMENT.get(diet);
            if (dietFoods != null) {
                recommendedFoods.addAll(dietFoods);
            }
        }
        return new ArrayList<>(recommendedFoods);
    }

    public static List<String> getAvoidedFoods(Set<Answer> answers, Set<Vegetarian> vegetarians) {
        Set<String> avoidedFoods = new HashSet<> ();

        // 답변 - 피해야 되는 음식 추가
        for (Answer answer : answers) {
            List<String> answerFoods = FoodMappingConfig.ANSWER_FOOD_AVOIDANCE.get(answer);
            if (answerFoods != null) {
                avoidedFoods.addAll(answerFoods);
            }
        }
        // 베지테리언 - 피해야 되는 음식 추가
        for (Vegetarian vegetarian : vegetarians) {
            List<String> vegetFoods = FoodMappingConfig.VEGETARIAN_FOOD_AVOIDANCE.get(vegetarian);
            if (vegetFoods != null) {
                avoidedFoods.addAll(vegetFoods);
            }
        }

        return new ArrayList<> (avoidedFoods);
    }

    public static List<String> getFinalFoods (Set<Answer> answers, Set<Vegetarian> vegetarians, Set<Diet> diets) {
        //필요한 옥식
        Set<String> recommendedFoods = new HashSet<>(getRecommendedFoods(answers, vegetarians, diets));
        //피해야 할 음식
        Set<String> avoidedFoods = new HashSet<>(getAvoidedFoods(answers, vegetarians));

        //최종 추천 음식: recommendedFoods - avoidedFoods
        recommendedFoods.removeAll(avoidedFoods);

        return new ArrayList<>(recommendedFoods);
    }
}
