package healeat.server.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberDiseaseRequestDto {

    List<Long> diseaseIds;
}
