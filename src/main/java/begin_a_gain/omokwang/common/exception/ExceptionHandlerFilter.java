package begin_a_gain.omokwang.common.exception;

import begin_a_gain.omokwang.common.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;


public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
            setErrorResponse(response, ex.getErrorCode().getHttpStatus(), ex.getMessage());
        } catch (Exception ex) {
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response,
                                  HttpStatus status,
                                  String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        CommonResponse<Void> errorResponse = CommonResponse.error(status.value(), message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

