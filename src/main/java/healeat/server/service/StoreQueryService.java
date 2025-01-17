package healeat.server.service;

import healeat.server.domain.Store;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.mapping.Review;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface StoreQueryService {

    int getApiCallCount();

    List<KakaoPlaceResponseDto.Document> searchByFilter(@CheckSizeSum StoreRequestDto.SearchFilterDto request);

    Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder);
}
