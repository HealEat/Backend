package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StoreResonseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorePreviewListDto {

        List<StorePreviewDto> storeList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorePreviewDto {

        Long storeId;
        String storeName;
        String kindName; // 음식 종류

        Boolean isBookMarked;

        Integer reviewCount; // 리뷰 수

        Float totalScore;
        Float sickCareScore; // 질병 보유자 점수
        Float vegetScore; // 베지테리언 점수
        Float dietScore; // 다이어터 점수

        List<String> allFeatureNames; // 음식 특징

        String kakaoMapUrl; // 카카오맵으로 열기
    }
}
