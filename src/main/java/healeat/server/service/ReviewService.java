package healeat.server.service;

import healeat.server.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /// 리뷰 : 동적 정렬, 조회 서비스

    /// 리뷰 이미지 : 최신순 조회 서비스 -> 객체 리스트 반환. 여러 곳에 쓰임
}
