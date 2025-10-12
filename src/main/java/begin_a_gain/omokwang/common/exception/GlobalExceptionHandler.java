package begin_a_gain.omokwang.common.exception;

import begin_a_gain.omokwang.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("[CustomException] errCode: {}", ex.getErrorCode());
        log.error("[CustomException] errMsg: {}", ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ErrorResponse.of(
                        ex.getErrorCode().getHttpStatus().value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("[RuntimeException] errMsg: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[Exception] errMsg: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "error"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(Exception ex) {
        log.error("[Exception] errMsg: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}

