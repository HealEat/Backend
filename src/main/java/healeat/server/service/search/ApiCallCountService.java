package healeat.server.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ApiCallCountService {

    private final AtomicInteger apiCallCount = new AtomicInteger(0);

    public void incrementApiCallCount() {
        apiCallCount.incrementAndGet(); // 원자적으로 증가
    }

    public int getAndResetApiCallCount() {
        return apiCallCount.getAndSet(0); // 원자적으로 값 반환 후 초기화
    }
}