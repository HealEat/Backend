package healeat.server.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreApiCallService {
    private int apiCallCount = 0;

    public void incrementApiCallCount() {
        apiCallCount++;
    }

    public int getAndResetApiCallCount() {
        int count = apiCallCount;
        apiCallCount = 0;
        return count;
    }
}