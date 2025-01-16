package healeat.server.config;

import healeat.server.domain.enums.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodMappingConfig {

    public static final Map<Answer, List<String>> FOOD_ASSIGNMENT = new HashMap<>();
    public static final Map<Answer, List<String>> FOOD_AVOIDANCE = new HashMap<>();

    static {
        //필요 음식 매핑

        //질병으로 인해 겪는 불편에 대한 답변으로 필요한 음식 매핑
        //체력/신체 기능 감소
        FOOD_ASSIGNMENT.put(Answer.WEAKENED_PHYSIC, Arrays.asList("한식 > 육류/고기 > 갈비",
                "한식 > 육류/고기 > 닭요리", "한식 > 육류/고기 > 오리", "한식 > 육류/고기 > 불고기/두루치기",
                "한식 > 국밥", "한식 > 해물/생선", "한식 > 수제비", "한식 > 두부전문점", "한식 > 설렁탕",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 찌개/전골", "한식 > 감자탕", "한식 > 곰탕",
                "일식", "샤브샤브", "샐러드"));
        //체중 감소
        FOOD_ASSIGNMENT.put(Answer.WEIGHT_LOSS, Arrays.asList("한식 > 국수", "한식 > 육류/고기",
                "한식 > 국밥", "한식 > 해장국", "한식 > 해물/생선", "한식 > 수제비", "한식 > 두부전문점",
                "한식 > 순대", "한식 > 설렁탕", "한식 > 한정식", "한식 > 쌈밤", "한식 > 찌개/전골",
                "한식 > 감자탕", "한식 > 전골", "중식", "일식", "아시아 음식", "퓨전 요리", "샤브샤브",
                "패스트푸드 > 샌드위치", "샐러드"));
        //소화 불량
        FOOD_ASSIGNMENT.put(Answer.INDIGESTION, Arrays.asList("한식 > 닭요리", "한식 > 해물/생선 > 복어",
                "한식 > 해물/생선 > 아구", "한식 > 해물/생선 > 추어", "한식 > 두부전문점", "한식 > 설렁탕",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽", "한식 > 곰탕","샤브샤브", "퓨전요리 > 퓨전한식"));
        //식욕 부진
        FOOD_ASSIGNMENT.put(Answer.LOSS_OF_APPETITE, Arrays.asList("한식 > 육류/고기", "한식 > 국밥",
                "한식 > 해장국", "한식 > 해물/생선", "한식 > 설렁탕", "한식 > 한정식", "한식 > 쌈밥",
                "한식 > 냉면", "한식 > 찌개/전골", "한식 > 감자탕","한식 > 곰탕", "중식", "일식",
                "아시아 음식", "양식 > 멕시칸/브라질", "퓨전 요리", "샤브샤브", "패스트푸드 > 샌드위치"));
        //통증(두통, 관절통, 복통 등)
        FOOD_ASSIGNMENT.put(Answer.PAIN, Arrays.asList("한식 > 해물/생선", "한식 > 죽", "일식",
                "아시아 음식 > 인도 음식", "샤브샤브", "퓨전 요리 > 퓨전 한식"));
        //만성 피로
        FOOD_ASSIGNMENT.put(Answer.CHRONIC_FATIGUE, Arrays.asList("한식 > 국수 > 칼국수",
                "한식 > 닭요리 > 삼계탕", "한식 > 국밥", "한식 > 해장국", "한식 > 해물/생선", "한식 > 설렁탕",
                "한식 > 감자탕", "한식 > 곰탕", "아시아 음식 > 베트남 음식", "샤브샤브"));

        //건강 관리를 위해 필요한 식사에 대한 답변으로 필요한 음식 매핑
        //기름기 적은 식사
        FOOD_ASSIGNMENT.put(Answer.LOW_FAT, Arrays.asList("한식 > 국수", "한식 > 육류/고기 > 삼계탕",
                "한식 > 육류/고기 > 불고기/두루치기", "한식 > 해물/생선", "한식 > 두부전문점", "한식 > 설렁탕",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽", "한식 > 냉면", "한식 > 곰탕", "일식 > 초밥/롤",
                "퓨전 한식", "샤브샤브", "분식(김밥)", "샐러드", "패스트푸드 > 샌드위치"));
        //영양소가 고르게 포함된 식사
        FOOD_ASSIGNMENT.put(Answer.BALANCED,Arrays.asList("한식 > 육류/고기 > 삼계탕",
                "한식 > 육류/고기 > 불고기/두루치기", "한식 > 해물/생선 > 복어", "한식 > 해물/생선 > 아구",
                "한식 > 해물/생선 > 추어","한식 > 두부전문점", "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽",
                "샤브샤브", "패스트푸드 > 샌드위치", "퓨전 요리 > 퓨전 한식", "분식(김밥)", "샐러드"));
        //싱겁게 먹는 식사
        FOOD_ASSIGNMENT.put(Answer.LOW_SODIUM,Arrays.asList("한식 > 육류/고기 > 삼계탕",
                "한식 > 해물/생선 > 회", "한식 > 두부전문점", "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽",
                "일식 > 초밥/롤", "퓨전 요리 > 퓨전 한식", "패스트푸드 > 샌드위치", "샐러드"));
        //채소가 많은 식사
        FOOD_ASSIGNMENT.put(Answer.HIGH_VEGETABLE,Arrays.asList("한식 > 해물/생선 > 회",
                "한식 > 한정식", "한식 > 쌈밥", "일식 > 초밥/롤", "아시아 음식 > 베트남 음식", "분식(김밥)",
                "샤브샤브", "패스트푸드 > 샌드위치", "샐러드"));
        //소화가 잘 되는 식사 (위의 질문 소화 불량에 대한 내용과 동일)
        FOOD_ASSIGNMENT.put(Answer.EASY_TO_DIGEST,Arrays.asList("한식 > 닭요리", "한식 > 해물/생선 > 복어",
                "한식 > 해물/생선 > 아구", "한식 > 해물/생선 > 추어", "한식 > 두부전문점", "한식 > 설렁탕",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽", "한식 > 곰탕","샤브샤브", "퓨전요리 > 퓨전한식"));
        //에너지를 보충해주는 식사 (위의 질문 체력/신체 기능 감소에 대한 내용과 동일)
        FOOD_ASSIGNMENT.put(Answer.ENERGY,Arrays.asList("한식 > 육류/고기 > 갈비",
                "한식 > 육류/고기 > 닭요리", "한식 > 육류/고기 > 오리", "한식 > 육류/고기 > 불고기/두루치기",
                "한식 > 국밥", "한식 > 해물/생선", "한식 > 수제비", "한식 > 두부전문점", "한식 > 설렁탕",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 찌개/전골", "한식 > 감자탕", "한식 > 곰탕",
                "일식", "샤브샤브", "샐러드"));

        //건강 관리를 위해 특별히 필요한 영양소에 대한 답변으로 필요한 음식 매핑
        //탄수화물
        FOOD_ASSIGNMENT.put(Answer.CARBOHYDRATES,Arrays.asList("한식 > 국수", "한식 > 수제비",
                "한식 > 두부전문점", "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽", "일식",
                "아시아 음식 > 인도 음식", "아시아 음식 > 동남아 음식", "퓨전 요리 > 퓨전 한식", "샤브샤브",
                "패스트푸드 > 샌드위치", "샐러드"));
        //단백질
        FOOD_ASSIGNMENT.put(Answer.PROTEINS,Arrays.asList("한식 > 육류/고기", "한식 > 해물/생선",
                "한식 > 두부전문점", "한식 > 순대", "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽",
                "한식 > 감자탕", "일식 > 초밥/롤", "아시아 음식 > 인도 음식", "퓨전 요리 > 퓨전 한식",
                "샤브샤브", "샐러드"));
        //지방
        FOOD_ASSIGNMENT.put(Answer.FATS,Arrays.asList("한식 > 육류/고기 > 삼계탕", "한식 > 육류/고기 > 족발/보쌈",
                "한식 > 해물/생선", "한식 > 두부전문점", "한식 > 설렁탕", "한식 > 한정식", "한식 > 쌈밥",
                "한식 > 곰탕", "일식 > 초밥/롤", "샤브샤브"));
        //비타민
        FOOD_ASSIGNMENT.put(Answer.VITAMINS,Arrays.asList("한식 > 해물/생선 > 회", "한식 > 해물/생선 > 조개",
                "한식 > 해물/생선 > 굴/전복", "일식 > 초밥/롤", "아시아 음식 > 베트남 음식", "분식", "샤브샤브",
                "패스트푸드 > 샌드위치", "샐러드"));
        //무기질
        FOOD_ASSIGNMENT.put(Answer.MINERALS,Arrays.asList("한식 > 육류/고기 > 갈비",
                "한식 > 육류/고기 > 닭요리", "한식 > 육류/고기 > 족발/보쌈", "한식 > 육류/고기 > 불고기/두루치기",
                "한식 > 해물/생선", "한식 > 두부전문점", "한식 > 한정식", "한식 > 쌈밥", "일식 > 초밥/롤",
                "샤브샤브", "패스트푸드 > 샌드위치", "샐러드"));
        //섬유소
        FOOD_ASSIGNMENT.put(Answer.FIBRE,Arrays.asList("한식 > 쌈밥", "일식 > 초밥/롤",
                "아시아 음식 > 베트남 음식", "분식", "샤브샤브", "샐러드"));

        //베지테리언에 대한 답변으로 필요한 음식 매핑
        //폴로-페스코
        FOOD_ASSIGNMENT.put(Answer.POLO_PESCO,Arrays.asList("한식 > 국수", "한식 > 육류/고기 > 닭요리",
                "한식 > 육류/고기 > 오리", "한식 > 해물/생선", "한식 > 수제비", "한식 > 두부전문점",
                "한식 > 한정식", "한식 > 쌈밥", "한식 > 죽", "한식 > 냉면", "한식 > 찌개/전골",
                "일식 > 초밥/롤", "아시아 음식 > 인도 음식", "아시아 음식 > 동남아 음식", "분식",
                "샤브샤브", "패스트푸드 > 샌드위치", "샐러드"));
        //페스코
        FOOD_ASSIGNMENT.put(Answer.PESCO,Arrays.asList("한식 > 육류/고기 > 삼계탕",
                ));
        //폴로
        FOOD_ASSIGNMENT.put(Answer.POLO,Arrays.asList("한식 > 육류/고기 > 삼계탕",
                ));
        //락토-오보(비건과 동일)
        FOOD_ASSIGNMENT.put(Answer.LACTO_OVO,Arrays.asList("한식 > 해물/생선 > 회",
                ));
        //락토(비건과 동일)
        FOOD_ASSIGNMENT.put(Answer.LACTO,Arrays.asList("한식 > 닭요리", "한식 > 해물/생선 > 복어",
                ));
        //오보(비건과 동일)
        FOOD_ASSIGNMENT.put(Answer.OVO,Arrays.asList("한식 > 육류/고기 > 갈비",
               ));
        //비건
        FOOD_ASSIGNMENT.put(Answer.VEGAN,Arrays.asList("한식 > 육류/고기 > 갈비",
                ));

        //다이어트 목적에 대한 답변으로 필요한 음식 매핑
        //체중감량
        FOOD_ASSIGNMENT.put(Answer.LOSE_WEIGHT,Arrays.asList("한식 > 국수", ));

        //피할 음식 매핑
        //육류
        FOOD_AVOIDANCE.put(Answer.MEAT,Arrays.asList("한식 > 육류/고기", "한식 > 감자탕",
                "중식", "일식 > 돈가스/우동", "양식"));
        //밀가루
        FOOD_AVOIDANCE.put(Answer.WHEAT_BASED,Arrays.asList("한식 > 국수", "한식 > 수제비",
                "한식 > 냉면", "중식", "일식 > 일본식 라면", "아시아 음식 > 인도 음식", "양식", "분식 > 떡볶이",
                "퓨전 요리 > 퓨전 중식", "패스트푸드"));
        //날고기/생선
        FOOD_AVOIDANCE.put(Answer.RAW_MEAT_FISH,Arrays.asList("한식 > 회","일식 > 참치회",
                "일식 > 초밥/롤"));
        //생선류
        FOOD_AVOIDANCE.put(Answer.FISH,Arrays.asList("한식 > 해물/생선","일식 > 참치회",
                "일식 > 초밥/롤"));
        //연체류(굴,조개 등)
        FOOD_AVOIDANCE.put(Answer.MOLLUSK,Arrays.asList("한식 > 조개","한식 > 굴/전복"));
        //매운 음식
        FOOD_AVOIDANCE.put(Answer.SPICY,Arrays.asList("한식 > 찌개/전골","한식 > 감자탕",
                "분식 > 떡볶이"));
}
}
