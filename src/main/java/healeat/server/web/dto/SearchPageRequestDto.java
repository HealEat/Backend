package healeat.server.web.dto;

import healeat.server.domain.Store;
import healeat.server.domain.enums.Duration;
import healeat.server.domain.enums.SearchType;
import lombok.Getter;

public class SearchPageRequestDto {

    @Getter
    public static class SearchPageCreateRequestDto {
        private SearchType searchType;
        private Store store_id;
        private String query;
    }
}
