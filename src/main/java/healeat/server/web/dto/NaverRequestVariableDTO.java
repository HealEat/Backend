package healeat.server.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NaverRequestVariableDTO {

    String query;
    Integer display;
    Integer start;
    Integer sort;
}