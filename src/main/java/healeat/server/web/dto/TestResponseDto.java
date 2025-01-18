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
public class TestResponseDto {

    List<KakaoPlaceResponseDto.Document> documents;
    Integer count;
    Integer apiCallCount;
}
