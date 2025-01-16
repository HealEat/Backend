package healeat.server.service;

import healeat.server.domain.Store;
import healeat.server.domain.enums.Purpose;
import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.StoreRequestDto;
import org.springframework.data.domain.Page;

public interface StoreQueryService {

    Page<Store> searchByFilter(StoreRequestDto.SearchFilterDto request);

    Page<Review> getReviewList(Long storeId, Integer page, Purpose sort, String sortOrder);
}
