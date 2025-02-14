package healeat.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberDiseaseResponseDto {
    private Long memberId;
    private List<DiseaseInfo> diseases;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class DiseaseInfo {
        private Long id;
        private String name;
    }

    public static MemberDiseaseResponseDto from(Long memberId, List<DiseaseInfo> diseases) {
        return MemberDiseaseResponseDto.builder()
                .memberId(memberId)
                .diseases(diseases)
                .build();
    }
}
