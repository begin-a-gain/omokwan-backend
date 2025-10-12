package begin_a_gain.omokwang.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "공통 응답 포맷")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(
        @Schema(description = "HTTP 응답 코드", example = "200")
        int code,

        @Schema(description = "응답 상태", example = "success")
        String status,

        @Schema(description = "메시지", example = "요청 성공")
        String message,

        @Schema(description = "실제 데이터")
        T data
) {
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(HttpStatus.OK.value(), "success", "요청 성공", data);
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(HttpStatus.OK.value(), "success", "요청 성공", null);
    }

    public static <T> CommonResponse<T> success(int code, String message, T data) {
        return new CommonResponse<>(code, "success", message, data);
    }

    // 에러 응답
    public static <T> CommonResponse<T> error(int code, String message) {
        HttpStatus status =
                HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.INTERNAL_SERVER_ERROR;
        return new CommonResponse<>(code, status.name().toLowerCase(), message, null);
    }
}

