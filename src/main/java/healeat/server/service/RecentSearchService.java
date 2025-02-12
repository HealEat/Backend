package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.MemberHandler;
import healeat.server.apiPayload.exception.handler.RecentSearchHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.Member;
import healeat.server.domain.Store;
import healeat.server.domain.enums.SearchType;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.repository.MemberRepository;
import healeat.server.repository.RecentSearchRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.validation.annotation.CheckPage;
import healeat.server.web.dto.RecentSearchResponseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    public Page<RecentSearch> getRecentSearchPage(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 프론트는 생성일 기준으로 20개의 최근 검색 기록을 가져간다.
        Page<RecentSearch> recentSearchPage = recentSearchRepository
                .findAllByMember(member, PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));

        return recentSearchPage;
    }

    //혹시나 필요하면 쓸 getById
    public RecentSearch getRecentSearchById(Long id) {
        return recentSearchRepository.findById(id).orElseThrow(() ->
                new RecentSearchHandler(ErrorStatus.RECENT_SEARCH_NOT_FOUND));
    }

    //최근 검색 생성
    @Transactional
    public void saveRecentQuery(Member member, String query) {

        if (query == null || query.isBlank()) {
            return;
        }

        Optional<RecentSearch> optionalRecentSearch = recentSearchRepository.findByMemberAndQuery(member, query);
        if (optionalRecentSearch.isPresent()) {
                optionalRecentSearch.get().setUpdatedAt(LocalDateTime.now()); // 필드 수정 없이 updatedAt만 갱신
        } else {

            RecentSearch recentSearch = RecentSearch.builder()
                    .member(member)
                    .searchType(SearchType.QUERY)
                    .store(null)
                    .query(query)
                    .build();

            recentSearchRepository.save(recentSearch);
        }
    }

    @Transactional
    public RecentSearch saveRecentStore(Member member, Long placeId) {

        Optional<RecentSearch> optionalRecentSearch = recentSearchRepository.findByMemberAndId(member, placeId);
        if (optionalRecentSearch.isPresent()) {

            RecentSearch recentSearch = optionalRecentSearch.get();
            recentSearch.setUpdatedAt(LocalDateTime.now()); // 필드 수정 없이 updatedAt만 갱신
            return recentSearch;
        } else {

            Store store = storeRepository.findByKakaoPlaceId(placeId).orElseThrow(() ->
                    new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

            RecentSearch recentStore = RecentSearch.builder()
                    .searchType(SearchType.STORE)
                    .member(member)
                    .store(store)
                    .build();

            return recentSearchRepository.save(recentStore);
        }
    }

    //최근 검색 기록 삭제
    @Transactional
    public void deleteRecentSearch(Long id) {recentSearchRepository.deleteById(id); }

    public RecentSearchResponseDto.DeleteResultDto toDeleteRecentSearch(Long recentId) {
        RecentSearch deleteRecentSearch = getRecentSearchById(recentId);
        RecentSearchResponseDto.DeleteResultDto response = SearchPageConverter.toDeleteResultDto(deleteRecentSearch);

        deleteRecentSearch(recentId);

        return response;
    }
}
