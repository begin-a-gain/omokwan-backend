package begin_a_gain.omokwang.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED("E1001", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("E1002", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("E1003", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("E1004", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("E1005", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("E1006", HttpStatus.NOT_FOUND),
    MATCH_NOT_FOUND("E1007", HttpStatus.NOT_FOUND),
    MATCH_CAPACITY_FULL("E1008", HttpStatus.CONFLICT),
    NOT_FOUND("E1009", HttpStatus.NOT_FOUND),
    NOT_EXIST_USER("E1010", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("E1011", HttpStatus.FORBIDDEN);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
