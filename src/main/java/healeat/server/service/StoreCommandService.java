package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResponseDto;

public interface StoreCommandService {

    Store saveStore(SearchResultItem item);

    StoreResponseDto.StorePreviewDtoList searchAndMapStores(Member member,
                                                            Integer page,
                                                            StoreRequestDto.SearchKeywordDto request);

    StoreResponseDto.StorePreviewDtoList recommendAndMapStores(
            Member member,
            Integer page,
            String rect);

    StoreResponseDto.StorePreviewDtoList recommendAndMapStoresOld(
            Member member,
            Integer page,
            StoreRequestDto.HealEatRequestDtoOld request);
}
