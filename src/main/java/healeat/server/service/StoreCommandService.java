package healeat.server.service;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.validation.annotation.CheckSizeSum;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResponseDto;
import org.springframework.validation.annotation.Validated;

@Validated
public interface StoreCommandService {

    Store saveStore(SearchResultItem item);

    StoreResponseDto.StorePreviewDtoList searchAndMapStores(Member member,
                                                            @CheckPage Integer page,
                                                            @CheckSizeSum StoreRequestDto.SearchKeywordDto request);

    StoreResponseDto.StorePreviewDtoList recommendAndMapStores(
            Member member,
            @CheckPage Integer page,
            StoreRequestDto.HealEatRequestDto request);
}
