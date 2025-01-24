package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.repository.BookmarkRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.api_response.KakaoPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StoreRepository storeRepository;
    private final StoreApiClient storeApiClient;
    private final StoreConverter storeConverter;

    // 북마크 추가/삭제 토글
    @Transactional
    public void toggleBookmark(Member member, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndStore(member, store);

        if(existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .member(member)
                    .store(store)
                    .build();
            bookmarkRepository.save(bookmark);
        }
    }

//    // 사용자가 북마크한 가게 리스트 조회
//    @Transactional(readOnly = true)
//    public List<StoreResonseDto.StorePreviewDto> getBookmarkedStores(Member member) {
//        // 사용자의 북마크된 가게 id 조회
//        List<Long> storeIds = bookmarkRepository.findByMember(member)
//                .stream()
//                .map(bookmark -> bookmark.getStore().getId())
//                .collect(Collectors.toList());
//
//        List<StoreResonseDto.StorePreviewDto> storeList = new ArrayList<>();
//
//        for(Long storeId : storeIds) {
//            // 가게 정보 조회
//            KakaoPlaceResponseDto response = storeApiClient.getKakaoByQuery(storeId.toString(), "", "", 1, "accuracy", "FD6");
//            if(response.getDocuments().isEmpty()) {
//                continue;
//            }
//            KakaoPlaceResponseDto.Document document = response.getDocuments().get(0);
//
//            // KakaoPlaceResponseDto 를 데이터를 StorePreviewDto 로 변환
//            StoreResonseDto.StorePreviewDto storePreviewDto = StoreResonseDto.StorePreviewDto.builder()
//                    .placeid(Long.parseLong(document.getId()))
//                    .place_name(document.getPlace_name())
//                    .category_name(document.getCategory_name())
//                    .phone(document.getPhone())
//                    .address_name(document.getAddress_name())
//                    .road_address_name(document.getRoad_address_name())
//                    .x(document.getX())
//                    .y(document.getY())
//                    .place_url(document.getPlace_url())
//                    .distance(document.getDistance())
//                    .isBookMarked(true)
//                    .reviewCount(0)
//                    .totalScore(0.0f)
//                    .features(Collections.emptyList())
//                    .build();
//            storeList.add(storePreviewDto);
//        }
//        return storeList;
//    }
}
