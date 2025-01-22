package healeat.server.service.search;

import healeat.server.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SearchResultScheduler {

    private final SearchResultRepository searchResultRepository;

    // 매 시간 정각에 실행
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void removeExpiredSearchResults() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        searchResultRepository.deleteByCreatedAtBefore(thirtyMinutesAgo);
    }
}
