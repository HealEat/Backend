package healeat.server.converter;

import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.search.SearchResult;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class StoreConverter {

    public static StoreResonseDto.SearchInfoDto toSearchInfo(Member member,
                                                             SearchResult searchResult,
                                                             int apiCallCount) {
        String memberName = null;
        List<String> healEatFoods = null;
        if (member != null) {
            memberName = member.getName();
            healEatFoods = member.getHealEatFoods();
        }

        boolean hasHealthInfo = (healEatFoods != null) && (!healEatFoods.isEmpty());
        return StoreResonseDto.SearchInfoDto.builder()
                .memberName(memberName)
                .hasHealthInfo(hasHealthInfo)
                .baseX(searchResult.getBaseX())
                .baseY(searchResult.getBaseY())
                .radius(searchResult.getRadius())
                .query(searchResult.getQuery())
                .otherRegions(searchResult.getOtherRegions())
                .selectedRegion(searchResult.getSelectedRegion())

                .apiCallCount(apiCallCount)
                .build();
    }

    public static StoreResonseDto.StorePreviewDtoList toStorePreviewListDto(
            Page<StoreResonseDto.StorePreviewDto> storePage, StoreResonseDto.SearchInfoDto searchInfoDto) {

        return StoreResonseDto.StorePreviewDtoList.builder()
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
