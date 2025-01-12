package healeat.server.web.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class HealthPlanResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanListDto{
       private List<HealthPlanOneDto> HealthPlanList = new ArrayList<>();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthPlanOneDto{

        private Long name;
        private String duration;
        private Integer goalNumber;
        private Integer count;
        private String goal;
        private String memo;
        private List<MemoImageResponseDto> memoImages;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoImageResponseDto {
        private String imageUrl;
    }

}
