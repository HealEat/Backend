package healeat.server.service;

import healeat.server.converter.SearchPageConverter;
import healeat.server.domain.FoodCategory;
import healeat.server.domain.FoodFeature;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.RecentSearch;
import healeat.server.web.dto.RecentSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchPageService {

    private final CategoryFeatureService categoryFeatureService;
    private final RecentSearchService recentSearchService;

    public RecentSearchResponseDto getRecentSearch(Member member) {

        //최근 검색 리스트 모두 가져오기
        List<RecentSearch> recentSearches = recentSearchService.getAllRecentSearches(member);

        //최근 검색 DTO 에 맞게 변환 후 리스트로 만들기
        List<RecentSearchResponseDto.RecentSearchDto> recentSearchList = recentSearches.stream()
                .map(SearchPageConverter::toRecentSearchDto)
                .collect(Collectors.toList());


        return RecentSearchResponseDto.builder()
                .recentSearchList(recentSearchList)
                .build();
    }
}
