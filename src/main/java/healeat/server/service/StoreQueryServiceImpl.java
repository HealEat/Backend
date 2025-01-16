package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.SortHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.domain.Store;
import healeat.server.domain.enums.Diet;
import healeat.server.domain.enums.SortBy;
import healeat.server.domain.enums.Vegetarian;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.ReviewRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryServiceImpl implements StoreQueryService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final StoreApiClient storeApiClient;

    @Override
    public Page<Store> searchByFilter(StoreRequestDto.SearchFilterDto request) {

        int adjustedPage = Math.max(0, request.getPage() - 1);

        KakaoPlaceResponseDto kakaoResponse = storeApiClient.getStoresByQuery(
                request.getUserInput(),
                request.getX(),
                request.getY(),
                adjustedPage,
                "accuracy");

        if (request.getFeature() != null) {
            return null; // 구현 필요
        } else {

            return null; // 구현 필요
        }
    }

    @Override
    public Page<Review> getReviewList(Long storeId, Integer page, SortBy sort, String sortOrder) {

        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        if (sort == null) sort = SortBy.LATEST;

        // 페이지 번호를 0-based로 조정
        int adjustedPage = Math.max(0, page - 1);

        Sort.Direction direction = getSortDirection(sortOrder);

        Sort sorting;
        PageRequest pageable;

        switch (sort) {
            case SICK:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_DiseasesNotEmpty(store, pageable);
            case VEGET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Vegetarian(store, Vegetarian.NONE, pageable);
            case DIET:
                sorting = Sort.by(direction, "totalScore", "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStoreAndMember_Diet(store, Diet.NONE, pageable);
            case LATEST:
                sorting = Sort.by(direction, "createdAt");
                pageable = PageRequest.of(adjustedPage, 10, sorting);
                return reviewRepository.findAllByStore(store, pageable);
            default:
                throw new SortHandler(ErrorStatus.SORT_NOT_FOUND);
        }
    }

    private Sort.Direction getSortDirection(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
