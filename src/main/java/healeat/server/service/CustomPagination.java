package healeat.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomPagination {

    public static <T> Page<T> toPage(List<T> list, int page, int size, Comparator<T> comparator) {
        // 기본 유효성 검증
        if (list == null || list.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        // 시작 인덱스와 종료 인덱스 계산
        int start = page * size;
        if (start >= list.size()) {
            return Page.empty(PageRequest.of(page, size));
        }

        int end = Math.min(start + size, list.size());

        // 필요한 부분만 스트림으로 정렬 및 수집
        List<T> sortedSubList = list.subList(start, end).stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return new PageImpl<>(sortedSubList, PageRequest.of(page, size), list.size());
    }
}
