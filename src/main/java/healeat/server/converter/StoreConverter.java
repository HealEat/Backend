package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.domain.search.SearchResult;
import healeat.server.web.dto.StoreResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class StoreConverter {

    public static StoreResponseDto.SearchInfoDto toSearchInfo(Member member,
                                                              SearchResult searchResult,
                                                              int apiCallCount) {
        String memberName = null;
        List<String> healEatFoods = null;
        if (member != null) {
            memberName = member.getName();
            healEatFoods = member.getHealEatFoods();
        }

        boolean hasHealthInfo = (healEatFoods != null) && (!healEatFoods.isEmpty());
        return StoreResponseDto.SearchInfoDto.builder()
                .memberName(memberName)
                .hasHealthInfo(hasHealthInfo)

                .query(searchResult.getQuery())
                .baseX(searchResult.getBaseX())
                .baseY(searchResult.getBaseY())
                .radius(searchResult.getRadius())

                .avgX(searchResult.getAvgX())
                .avgY(searchResult.getAvgY())
                .maxMeters(searchResult.getMaxMeters())

                .otherRegions(searchResult.getOtherRegions())
                .selectedRegion(searchResult.getSelectedRegion())

                .apiCallCount(apiCallCount)
                .build();
    }

    public static StoreResponseDto.StorePreviewDtoList toStorePreviewListDto(
            Page<StoreResponseDto.StorePreviewDto> storePage, StoreResponseDto.SearchInfoDto searchInfoDto) {

        return StoreResponseDto.StorePreviewDtoList.builder()
                .storeList(storePage.getContent())
                .listSize(storePage.getNumberOfElements())
                .totalPage(storePage.getTotalPages())
                .totalElements(storePage.getTotalElements())
                .isFirst(storePage.isFirst())
                .isLast(storePage.isLast())
                .searchInfoDto(searchInfoDto)
                .build();
    }
}
