package healeat.server.converter;

import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreConverter {

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

    // 가게 정보를 StorePreviewDto 로 변환 (북마크 포함)
    public StoreResonseDto.StorePreviewDto toStorePreviewDto(KakaoPlaceResponseDto.Document document, boolean isBookmarked) {
        return StoreResonseDto.StorePreviewDto.builder()
                .id(Long.parseLong(document.getId()))
                .place_name(document.getPlace_name())
                .category_name(document.getCategory_name())
                .phone(document.getPhone())
                .address_name(document.getAddress_name())
                .road_address_name(document.getRoad_address_name())
                .x(document.getX())
                .y(document.getY())
                .place_url(document.getPlace_url())
                .distance(document.getDistance())
                .isBookMarked(isBookmarked)
                .reviewCount(0)
                .totalScore(0.0f)
                .features(List.of())  // 기본 빈 리스트
                .build();
    }

    // 북마크된 가게 리스트 변환
    public List<StoreResonseDto.StorePreviewDto> toStorePreviewDtoList(List<KakaoPlaceResponseDto.Document> documents, List<Long> bookmarkedIds) {
        return documents.stream()
                .map(document -> toStorePreviewDto(document, bookmarkedIds.contains(Long.parseLong(document.getId()))))
                .collect(Collectors.toList());
    }
}
