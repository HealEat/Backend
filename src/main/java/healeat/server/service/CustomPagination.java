package healeat.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;

public class CustomPagination {

    public static <T> Page<T> toPage(List<T> list, int page, int size, Comparator<T> comparator) {

        int start = Math.max(0, (page - 1) * size);
        int end = Math.min(start + size, list.size());

        // 정렬 적용
        List<T> sortedList = list.stream()
                .sorted(comparator)
                .toList();

        if (start >= list.size()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), list.size());
        }

        List<T> subList = sortedList.subList(start, end);
        return new PageImpl<>(subList, PageRequest.of(page, size), list.size());
    }
}
