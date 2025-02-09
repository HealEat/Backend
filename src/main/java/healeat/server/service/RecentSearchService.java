package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.RecentSearchHandler;
import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.Member;
import healeat.server.domain.enums.SearchType;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.repository.RecentSearchRepository;
import healeat.server.web.dto.RecentSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;

    public List<RecentSearch> getAllRecentSearches(Member member) {
        List<RecentSearch> recentSearches = recentSearchRepository.findByMember(member);

        // 내림차순 정렬 (가장 최근 생성된 항목이 앞쪽)
        recentSearches.sort(Comparator.comparing(RecentSearch::getCreatedAt).reversed());

        return recentSearches;
    }

    //혹시나 필요하면 쓸 getById
    public RecentSearch getRecentSearchById(Long id) {
        return recentSearchRepository.findById(id).orElseThrow(() ->
                new RecentSearchHandler(ErrorStatus.RECENT_SEARCH_NOT_FOUND));
    }

    //최근 검색 생성
    @Transactional
    public void saveRecentQuery(Member member, String query) {

        RecentSearch recentSearch = RecentSearch.builder()
                .member(member)
                .searchType(SearchType.QUERY)
                .store(null)
                .query(query)
                .build();

        recentSearchRepository.save(recentSearch);
    }

    @Transactional
    public RecentSearchResponseDto.SetResultDto saveRecentStore(Member member, Long storeId) {

        return null;
    }

    //최근 검색 기록 삭제
    @Transactional
    public void deleteRecentSearch(Long id) {recentSearchRepository.deleteById(id); }

    public RecentSearchResponseDto.toDeleteResultDto toDeleteRecentSearch(Long recentId) {
        RecentSearch deleteRecentSearch = getRecentSearchById(recentId);
        RecentSearchResponseDto.toDeleteResultDto response = SearchPageConverter.toDeleteResultDto(deleteRecentSearch);

        deleteRecentSearch(recentId);

        return response;
    }
}
