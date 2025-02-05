package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.ReviewHandler;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.ReviewRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    /// 리뷰 : 동적 정렬, 조회 서비스
    /// 리뷰 이미지 : 최신순 조회 서비스 -> 객체 리스트 반환. 여러 곳에 쓰임

    // 내가 남긴 후기 목록 조회 API
    @Transactional(readOnly = true)
    public ReviewResponseDto.myPageReviewListDto getMyReviews(Member member, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByMember(member, pageable);

        List<ReviewResponseDto.MyPageReviewDto> reviewsDtoList = reviewPage.stream().map(review ->
                ReviewResponseDto.MyPageReviewDto.builder()
                        .storeName(review.getStore().getPlaceName())
                        .reviewPreview(ReviewConverter.toReviewPreviewDto(review))
                        .build()
        ).collect(Collectors.toList());

        return ReviewResponseDto.myPageReviewListDto.builder()
                .myPageReviewList(reviewsDtoList)
                .listSize(reviewsDtoList.size())
                .totalPage(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .isFirst(reviewPage.isFirst())
                .isLast(reviewPage.isLast())
                .build();
    }

    // 특정 리뷰 삭제 API
    @Transactional
    public ReviewResponseDto.DeleteResultDto deleteReview(Member member, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));

        reviewRepository.delete(review);

        return ReviewResponseDto.DeleteResultDto.builder()
                .deletedReviewId(reviewId)
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
