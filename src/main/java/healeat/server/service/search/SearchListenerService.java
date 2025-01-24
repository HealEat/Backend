package healeat.server.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class SearchListenerService {

    private final AtomicInteger apiCallCount = new AtomicInteger(0);
    private final AtomicLong newFeatureId = new AtomicLong(0L);

    public void incrementApiCallCount() {
        apiCallCount.incrementAndGet(); // 원자적으로 증가
    }

    public int getAndResetApiCallCount() {
        return apiCallCount.getAndSet(0); // 원자적으로 값 반환 후 초기화
    }

    public void setNewFeatureId(Long id) {
        newFeatureId.set(id);
    }

    public long getAndResetFeatureId() {

        return newFeatureId.getAndSet(0L);
    }
}