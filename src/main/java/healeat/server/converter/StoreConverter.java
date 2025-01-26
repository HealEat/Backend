package healeat.server.converter;

import healeat.server.domain.Store;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class StoreConverter {

    public static StoreResonseDto.SetResultDto toSetResultDto(Store store) {

        return StoreResonseDto.SetResultDto.builder()
                .storeId(store.getId())
                .placeName(store.getPlaceName())
                .createdAt(store.getCreatedAt())
                .build();
    }

    public static StoreResonseDto.StorePreviewDtoList toStorePreviewListDto(
            Pair<Page<StoreResonseDto.StorePreviewDto>, StoreResonseDto.SearchInfo> pair) {

        Page<StoreResonseDto.StorePreviewDto> storePage = pair.getFirst();
        StoreResonseDto.SearchInfo searchInfo = pair.getSecond();

        return StoreResonseDto.StorePreviewDtoList.builder()
                .storeList(storePage.getContent())
                .listSize(storePage.getNumberOfElements())
                .totalPage(storePage.getTotalPages())
                .totalElements(storePage.getTotalElements())
                .isFirst(storePage.isFirst())
                .isLast(storePage.isLast())
                .searchInfo(searchInfo)
                .build();
    }
}
