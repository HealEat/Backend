package healeat.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomPagination {

    /**
     * 정렬 없이 페이지네이션만 수행합니다.
     */
    static <T> Page<T> toPage(List<T> list, int page, int size) {
        if (list == null || list.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        int totalSize = list.size();
        int start = page * size;

        if (start >= totalSize) {
            return Page.empty(PageRequest.of(page, size));
        }

        int end = Math.min(start + size, totalSize);
        List<T> subList = list.subList(start, end);

        return new PageImpl<>(subList, PageRequest.of(page, size), totalSize);
    }
}
