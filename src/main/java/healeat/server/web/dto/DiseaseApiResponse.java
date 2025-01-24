package healeat.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiseaseApiResponse {

    @JsonProperty("body")
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("items")
        private List<Item> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("sickNm")
        private String sickNm;
    }

    // <body>의 <items>에서 <item> 안의 <SickNm>질병명</SickNm> 추출
    public List<String> extractDiseaseNames() {
        return body.getItems().stream()
                .map(Item::getSickNm)
                .collect(Collectors.toList());
    }
}
