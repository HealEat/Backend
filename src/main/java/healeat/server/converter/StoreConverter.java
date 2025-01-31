package healeat.server.converter;

import healeat.server.domain.Store;
import healeat.server.domain.search.SearchResult;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class StoreConverter {

    public static StoreResonseDto.SearchInfo toSearchInfo(SearchResult searchResult,
                                                   int apiCallCount) {

        return StoreResonseDto.SearchInfo.builder()
                .baseX(searchResult.getBaseX())
                .baseY(searchResult.getBaseY())
                .query(searchResult.getQuery())
                .otherRegions(searchResult.getOtherRegions())
                .selectedRegion(searchResult.getSelectedRegion())

                .apiCallCount(apiCallCount)
                .build();
    }

    public static StoreResonseDto.SetResultDto toSetResultDto(Store store) {

        return StoreResonseDto.SetResultDto.builder()
                .storeId(store.getId())
                .placeName(store.getPlaceName())
                .createdAt(store.getCreatedAt())
                .build();
    }

    public static StoreResonseDto.StorePreviewDtoList toStorePreviewListDto(
            Page<StoreResonseDto.StorePreviewDto> storePage, StoreResonseDto.SearchInfo searchInfo) {

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
