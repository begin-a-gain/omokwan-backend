package begin_a_gain.omokwang.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "에러 응답 포맷")
public record ErrorResponse(
        @Schema(description = "HTTP 응답 코드")
        int code,

        @Schema(description = "에러 상태")
        String status,

        @Schema(description = "에러 메시지")
        String message
) {
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), status.name().toLowerCase(), message);
    }

    public static ErrorResponse of(int code, String message) {
        HttpStatus httpStatus =
                HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ErrorResponse(code, httpStatus.name().toLowerCase(), message);
    }
}
