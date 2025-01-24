package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    Float tastyScore;
    Float cleanScore;
    Float freshScore;
    Float nutrScore;

    List<String> imageUrls;
    String body;
}
