package healeat.server.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import healeat.server.apiPayload.code.BaseErrorCode;
import healeat.server.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 검색 관련 응답
    FILTERS_LESS_EQUAL_THAN_5(HttpStatus.BAD_REQUEST, "SEARCH4001", "페이지 번호는 1 이상이어야 합니다."),

    // 페이징 관련 응답
    PAGE_NUM_NOT_NATURAL(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지 번호는 1 이상이어야 합니다."),

    // 정렬 쿼리 스트링 관련 응답
    SORT_NOT_FOUND(HttpStatus.BAD_REQUEST, "SORT4001", "정렬 쿼리 스트링이 적절하지 않습니다."),

    // 가게 음식 카테고리 관련 응답
    FOOD_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "FOOD_CATEGORY4001", "없는 음식 카테고리입니다."),

    // 가게 관련 응답
    STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORE4001", "DB에 가게가 없습니다."),

    // 리뷰 관련 응답
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4001", "리뷰가 없습니다."),
    REVIEW_TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "REVIEW4002", "등록할 수 있는 리뷰 이미지 개수를 초과하였습니다."),
    IS_NOT_REVIEW_AUTHOR(HttpStatus.BAD_REQUEST, "REVIEW4003", "리뷰 작성자가 아니므로 삭제할 권한이 없습니다."),


    // 건강 목표 관련 응답
    HEALTH_PLAN_NOT_FOUND(HttpStatus.BAD_REQUEST, "HEALTH_PLAN4001", "건강 목표가 없습니다."),

    // 건강 목표 설정 시 목표 횟수가 1 ~ 10 이 아닐 때
    HEALTH_PLAN_GOAL_NUMBER(HttpStatus.BAD_REQUEST, "HEALTH_PLAN4002", "유효하지 않은 목표 횟수입니다."),

    // 건강 목표 이미지가 5개가 넘을 때
    HEALTH_PLAN_TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "HEALTH_PLAN4003", "넣을 수 있는 이미지 개수를 초과하였습니다."),

    // 회원 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),

    // 회원 건강 정보 설정 관련 응답
    QUESTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "HEALTH_INFO4001", "해당 질문이 없습니다."),
    ANSWER_NOT_FOUND(HttpStatus.BAD_REQUEST, "HEALTH_INFO4002", "해당 답변이 없습니다."),

    // 최근 검색 관련 응답
    RECENT_SEARCH_NOT_FOUND(HttpStatus.BAD_REQUEST, "RECENT_SEARCH4001", "최근 검색 기록 없습니다."),

    // 음식 특징 관련 응답
    FOOD_FEATURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FOOD_FEATURE4001", "음식 특징이 없습니다."),

    // 이미지 종류 관련 응답
    IMAGE_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE4001", "해당 이미지 종류가 없습니다."),
    IMAGE_INVALID_PUBLIC_URL(HttpStatus.BAD_REQUEST, "IMAGE4002", "해당 이미지의 PUBLIC URL이 유효하지 않습니다."),
    HEALTH_PLAN_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE4003", "해당 건강 관리 목표 이미지가 없습니다."),
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE4004", "해당 프로필 이미지가 없습니다."),
    REVIEW_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE4005", "해당 리뷰 이미지가 없습니다."),

    // 약관 관련 응답
    INVALID_TERM_ID(HttpStatus.BAD_REQUEST, "TERM4001", "유효하지 않은 약관입니다."),

    // 질병 관련 응답
    DISEASE_NOT_FOUND(HttpStatus.BAD_REQUEST, "DISEASE4001", "질병을 찾을 수 없습니다."),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "DISEASE4002", "이미 저장된 질병입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
