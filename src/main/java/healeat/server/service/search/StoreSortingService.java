package healeat.server.service.search;

import healeat.server.web.dto.StoreResonseDto;
import healeat.server.web.dto.StoreResonseDto.StorePreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreSortingService {

    public List<StorePreviewDto> sortStoresByPreference(
            List<StorePreviewDto> stores,
            Float minRating) {
        // 정렬 I. 리뷰가 존재하는 가게들 우선
        List<StorePreviewDto> serverFirstList = filterAndSortByReviews(stores, minRating);
        List<StorePreviewDto> remainList = getStoresWithoutReviews(stores);

        serverFirstList.addAll(remainList);
        // 정렬 II. 거리 순, 전체 평점 높은 순
        return sortByDistanceAndScore(serverFirstList);
    }

    private List<StorePreviewDto> filterAndSortByReviews(
            List<StorePreviewDto> stores,
            Float minRating) {
        return new ArrayList<>(stores.stream()
                .filter(s -> (s.getReviewCount() > 0) && (s.getTotalScore() >= minRating))
                .toList());
    }

    private List<StorePreviewDto> getStoresWithoutReviews(List<StorePreviewDto> stores) {
        return stores.stream()
                .filter(s -> s.getReviewCount() == 0)
                .toList();
    }

    private List<StorePreviewDto> sortByDistanceAndScore(List<StorePreviewDto> stores) {
        return stores.stream()
                .sorted(createStoreComparator())
                .toList();
    }

    private Comparator<StorePreviewDto> createStoreComparator() {
        return Comparator
                .comparing((StorePreviewDto s) -> {
                    String distance = s.getDistance();
                    return distance == null || distance.isEmpty()
                            ? Integer.MAX_VALUE
                            : Integer.parseInt(distance);
                })
                .thenComparing(StorePreviewDto::getTotalScore, Comparator.reverseOrder());
    }
}
