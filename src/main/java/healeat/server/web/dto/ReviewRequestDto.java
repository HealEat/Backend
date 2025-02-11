package healeat.server.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class ReviewRequestDto {

    @Max(value = 5, message = "건강 평점은 5 이하여야 합니다")
    @Min(value = 1, message = "건강 평점은 1 이상이어야 합니다")
    Float healthScore;

    Float tastyScore;
    Float cleanScore;
    Float freshScore;
    Float nutrScore;

    @Size(max = 300, message = "리뷰는 최대 300자까지 쓸 수 있습니다.")
    @Valid
    String body;

    @Getter
    public static class ReviewImageRequestDto {
        String imageExtension; // 파일 확장자
    }
}
