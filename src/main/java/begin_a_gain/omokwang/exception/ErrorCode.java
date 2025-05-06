package begin_a_gain.omokwang.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED("인증되지않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("유효하지않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DAEGUK_NOT_FOUND("대국을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_EXIST_USER("존재하지 않는 유저입니다.", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
