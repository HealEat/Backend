package healeat.server.web.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class MemberProfileRequestDto {

    private String name;
    private String profileImageUrl;
}
