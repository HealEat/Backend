package healeat.server.converter;

import healeat.server.domain.mapping.Review;
import healeat.server.web.dto.StoreResonseDto;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReviewConverter {

    public static StoreResonseDto.ReviewPreviewDto toReviewPreviewDto(Review review) {

        Map<Purpose, List<String>> currentPurposes = review.getCurrentPurposes();
        List<String> purposeStringList = new ArrayList<>();
        List<Purpose> orderedPurposes = Arrays.asList(Purpose.SICK, Purpose.VEGET, Purpose.DIET);
        if (currentPurposes != null) {
            orderedPurposes.forEach(purpose -> {
                List<String> values = currentPurposes.get(purpose);
                if (values != null) {
                    purposeStringList.addAll(values);
                }
            });
        }

        return StoreResonseDto.ReviewPreviewDto.builder()
                .name(review.getMember().getName())
                .currentPurposes(purposeStringList)
                .reviewId(review.getId())
                .totalScore(review.getTotalScore())
                .images(/*review.getReviewImageList()*/ null) // 이미지 CRUD 구현 필요
                .body(review.getBody())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static StoreResonseDto.ReviewPreviewListDto toReviewPreviewListDto(Page<Review> reviewPage) {

        List<StoreResonseDto.ReviewPreviewDto> reviewPreviewDtoList = reviewPage.stream()
                .map(ReviewConverter::toReviewPreviewDto)
                .toList();

        return StoreResonseDto.ReviewPreviewListDto.builder()
                .reviewList(reviewPreviewDtoList)
                .listSize(reviewPreviewDtoList.size())
                .totalPage(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .isFirst(reviewPage.isFirst())
                .isLast(reviewPage.isLast())
                .build();
    }
}
