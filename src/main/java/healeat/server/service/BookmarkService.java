package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.converter.StoreConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Bookmark;
import healeat.server.repository.BookmarkRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.KakaoPlaceResponseDto;
import healeat.server.web.dto.StoreResonseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 사용자가 북마크한 가게 리스트 조회
    @Transactional(readOnly = true)
    public List<StoreResonseDto.StorePreviewDto> getBookmarkedStores(Member member) {
        // 사용자의 북마크된 가게 id 조회
        List<Long> storeIds = bookmarkRepository.findByMember(member)
                .stream()
                .map(bookmark -> bookmark.getStore().getId())
                .collect(Collectors.toList());

        // 북마크한 가게의 정보를 카카오 API를 통해 조회
        List<KakaoPlaceResponseDto.Document> documents = storeIds.stream()
                .map(storeId -> storeApiClient.getKakaoByQuery(storeId.toString(), "", "", 1, "accuracy", "FD6"))
                .filter(response ->!response.getDocuments().isEmpty())
                .map(response -> response.getDocuments().get(0))
                .collect(Collectors.toList());

        // StoreConverter 사용
        return storeConverter.toStorePreviewDtoList(documents, storeIds);
    }
}
