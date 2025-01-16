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

    // 페이징 관련 응답
    PAGE_NUM_NOT_NATURAL(HttpStatus.BAD_REQUEST, "PAGE4001", "페이지 번호는 1 이상이어야 합니다."),

    // 정렬 쿼리 스트링 관련 응답
    SORT_NOT_FOUND(HttpStatus.BAD_REQUEST, "SORT4001", "정렬 쿼리 스트링이 적절하지 않습니다."),

    // 가게 관련 응답
    STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORE4001", "가게가 없습니다."),

    // 리뷰 관련 응답
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4001", "리뷰가 없습니다."),

    // 건강 목표 관련 응답
    HEALTH_PLAN_NOT_FOUND(HttpStatus.BAD_REQUEST, "HEALTH_PLAN4001", "건강 목표가 없습니다."),

    // 회원 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),

    // 회원 건강 정보 설정 관련 응답
    VEGET_ANS_NOT_FOUND(HttpStatus.BAD_REQUEST, "HEALTH_INFO_ANS4001", "사용자의 베지테리언 답변이 없습니다."),

    // 질문 관련 응답
    QUESTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "QUESTION4001", "질문이 없습니다.")
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
