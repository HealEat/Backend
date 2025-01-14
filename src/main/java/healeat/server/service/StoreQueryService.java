package healeat.server.service;

import healeat.server.domain.Store;
import healeat.server.web.dto.StoreRequestDto;
import org.springframework.data.domain.Page;

public interface StoreQueryService {

    Page<Store> searchByFilter(StoreRequestDto.SearchFilterDto request);
}
