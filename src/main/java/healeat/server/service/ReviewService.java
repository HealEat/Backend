package healeat.server.service;

import healeat.server.apiPayload.code.status.ErrorStatus;
import healeat.server.apiPayload.exception.handler.ReviewHandler;
import healeat.server.apiPayload.exception.handler.StoreHandler;
import healeat.server.aws.s3.AmazonS3Manager;
import healeat.server.converter.ReviewConverter;
import healeat.server.domain.Member;
import healeat.server.domain.ReviewImage;
import healeat.server.domain.Store;
import healeat.server.domain.mapping.Review;
import healeat.server.repository.ReviewImageRepository;
import healeat.server.repository.ReviewRepository.ReviewRepository;
import healeat.server.repository.StoreRepository;
import healeat.server.web.dto.ReviewRequestDto;
import healeat.server.web.dto.ReviewResponseDto;
import healeat.server.web.dto.StoreRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final ReviewImageRepository reviewImageRepository;
    private final StoreRepository storeRepository;

    /// 리뷰 : 동적 정렬, 조회 서비스
    /// 리뷰 이미지 : 최신순 조회 서비스 -> 객체 리스트 반환. 여러 곳에 쓰임

    // 내가 남긴 후기 목록 조회 API
    @Transactional(readOnly = true)
    public ReviewResponseDto.MyPageReviewListDto getMyReviews(Member member, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByMemberOrderByCreatedAtDesc(member, pageable);

        List<ReviewResponseDto.MyPageReviewDto> reviewsDtoList = reviewPage.stream()
                .map(review -> ReviewResponseDto.MyPageReviewDto.builder()
                        .placeId(review.getPlaceId())
                        .placeName(review.getStore().getPlaceName())
                        .reviewPreview(ReviewConverter.toReviewPreviewDto(review))
                        .build()
                )
                .toList();

        return ReviewResponseDto.MyPageReviewListDto.builder()
                .myPageReviewList(reviewsDtoList)
                .listSize(reviewsDtoList.size())
                .totalPage(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .isFirst(reviewPage.isFirst())
                .isLast(reviewPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<Review> getStoreReviews(Long placeId, Integer page, String sortBy, List<String> filters) {

        if (filters.isEmpty())
            return Page.empty();

        Store store = storeRepository.findByKakaoPlaceId(placeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        // 페이지 번호를 0-based로 조정
        int safePage = Math.max(0, page - 1);

        Pageable pageable = PageRequest.of(safePage, 10);

        return reviewRepository.sortAndFilterReviews(
                store, sortBy, filters, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ReviewImage> getStoreReviewImages(Long placeId, Integer page) {

        Store store = storeRepository.findByKakaoPlaceId(placeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        // 페이지 번호를 0-based로 조정
        int safePage = Math.max(0, page - 1);

        Pageable pageable = PageRequest.of(safePage, 10);

        return reviewImageRepository.findAllByReview_StoreOrderByCreatedAtDesc(store, pageable);
    }

    // 리뷰 생성 API
    @Transactional
    public Review createReview(Long placeId, Member member, List<MultipartFile> files, ReviewRequestDto request) {

        Store store = storeRepository.findByKakaoPlaceId(placeId).orElseThrow(() ->
                new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

        Review review = Review.builder()
                .store(store)
                .placeId(placeId)
                .member(member)
                .healthScore(request.getHealthScore())
                .tastyScore(request.getTastyScore())
                .cleanScore(request.getCleanScore())
                .freshScore(request.getFreshScore())
                .nutrScore(request.getNutrScore())
                .body(request.getBody())
                .build();

        reviewRepository.save(review);

        if(!(files == null || files.isEmpty())){
            if (files.size() > 10) {
                throw new ReviewHandler(ErrorStatus.REVIEW_TOO_MANY_IMAGES);
            }
            for(MultipartFile file : files) {
                String keyName = amazonS3Manager.generateReviewKeyName();
                String uploadFileUrl = amazonS3Manager.uploadFile(keyName, file);

                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .imageUrl(uploadFileUrl)
                        .build();

                review.addImage(reviewImage);
                reviewImageRepository.save(reviewImage);
            }
        }

        return review;
    }

    /*
    * 특정 리뷰 삭제 API
    * 리뷰 삭제 시 S3의 이미지 같이 삭제
    */
    @Transactional
    public Review deleteReview(Member member, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));

        if (!review.getMember().equals(member)) {
            throw new ReviewHandler(ErrorStatus.IS_NOT_REVIEW_AUTHOR);
        }

        // S3 저장 이미지 삭제
        List<ReviewImage> reviewImages = review.getReviewImageList();
        for(ReviewImage reviewImage : reviewImages) {
            amazonS3Manager.deleteFile(reviewImage.getImageUrl());
            reviewImageRepository.delete(reviewImage);
        }

        // 가게 내 수치 조정
        Store store = review.getStore();
        store.deleteReview(review);

        // 리뷰 삭제
        reviewRepository.delete(review);
        return review;
    }
}
