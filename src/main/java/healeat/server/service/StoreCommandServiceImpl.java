package healeat.server.service;

import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.search.ItemDaumImage;
import healeat.server.domain.search.SearchResult;
import healeat.server.domain.search.SearchResultItem;
import healeat.server.repository.SearchResultItemRepository.SearchResultItemRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.service.search.*;
import healeat.server.web.dto.StoreRequestDto;
import healeat.server.web.dto.StoreResonseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreCommandServiceImpl implements StoreCommandService {

    private final StoreRepository storeRepository;
    private final SearchResultItemRepository searchResultItemRepository;

    private final StoreSearchService storeSearchService;
    private final ApiCallCountService apiCallCountService;
    private final SearchFeatureService searchFeatureService;
    private final StoreMappingService storeMappingService;
    private final DaumImageService daumImageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public Store saveStore(SearchResultItem item) {

        Store store = Store.builder()
                .kakaoPlaceId(item.getPlaceId())
                .placeName(item.getPlaceName())
                .categoryName(item.getCategoryName())
                .phone(item.getPhone())
                .addressName(item.getAddressName())
                .roadAddressName(item.getRoadAddressName())
                .x(item.getX())
                .y(item.getY())
                .placeUrl(item.getPlaceUrl())
                .features(item.getFeatures())
                .build();

        List<ItemDaumImage> daumImgDocuments = daumImageService.getDaumImgDocuments(store);
        daumImgDocuments.forEach(store::addItemDaumImage);

        return storeRepository.save(store);
    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     */
    @Override
    public StoreResonseDto.StorePreviewDtoList searchAndMapStores(
            Member member,
            Integer page,
            StoreRequestDto.SearchKeywordDto request) {

        // 1. 검색 및 결과 저장
        SearchResult searchResult = storeSearchService.searchAndSave(request);

        int apiCallCount = apiCallCountService.getAndResetApiCallCount(); // API 호출 횟수 기록

        StoreResonseDto.SearchInfoDto searchInfoDto = StoreConverter
                .toSearchInfo(member, searchResult, apiCallCount);

        // 2. category와 feature로 필터링된 placeId 리스트
        List<Long> filteredItemIds = searchFeatureService.getFilteredItemIds(
                searchResult.getItems(),
                request.getCategoryIdList(),
                request.getFeatureIdList()
        );

        if (filteredItemIds.isEmpty()) {
            return StoreConverter.toStorePreviewListDto(
                    Page.empty(),
                    searchInfoDto
            );

        } else {
            // 3. 페이지 요청 생성
            int safePage = Math.max(0, page - 1);
            Pageable pageable = PageRequest.of(safePage, 10);

            // 4. 정렬된 검색 결과 조회
            Page<SearchResultItem> items = searchResultItemRepository.findSortedStores(
                    searchResult,
                    filteredItemIds,
                    request.getSortBy(),
                    request.getMinRating(),
                    pageable
            );

            // 5. DTO 변환 및 결과 반환
            return StoreConverter.toStorePreviewListDto(
                    items.map(item -> storeMappingService.mapToDto(member, item)),
                    searchInfoDto
            );
        }
    }

    /**
     * 핵심 로직 이후
     * 정렬 및 페이징 적용
     */
    @Override
    public StoreResonseDto.StorePreviewDtoList recommendAndMapStores(
            Member member,
            Integer page,
            StoreRequestDto.HealEatRequestDto request) {

        // 1. 검색 및 결과 저장
        SearchResult searchResult = storeSearchService.recommendAndSave(request);

        int apiCallCount = apiCallCountService.getAndResetApiCallCount(); // API 호출 횟수 기록

        StoreResonseDto.SearchInfoDto searchInfoDto = StoreConverter
                .toSearchInfo(member, searchResult, apiCallCount);

        // 2. 멤버의 healEatFoods(카테고리 이름 리스트) 로 필터링된 placeId 리스트
        List<Long> filteredItemIds = searchFeatureService.getHealEatItemIds(
                searchResult.getItems(),
                member.getHealEatFoods()
        );

        if (filteredItemIds.isEmpty()) {
            return StoreConverter.toStorePreviewListDto(
                    Page.empty(),
                    searchInfoDto
            );

        } else {
            // 3. 페이지 요청 생성
            int safePage = Math.max(0, page - 1);
            Pageable pageable = PageRequest.of(safePage, 10);

            // 4. 정렬된 검색 결과 조회
            Page<SearchResultItem> items = searchResultItemRepository.findSortedStores(
                    searchResult,
                    filteredItemIds,
                    "NONE",
                    0.0f,
                    pageable
            );

            // 5. DTO 변환 및 결과 반환
            return StoreConverter.toStorePreviewListDto(
                    items.map(item -> storeMappingService.mapToDto(member, item)),
                    searchInfoDto
            );
        }
    }
}
