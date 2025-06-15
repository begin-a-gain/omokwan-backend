package begin_a_gain.omokwang.match.dto.match_board;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DateStatus(
        @Schema(description = "날짜 (yyyy-MM-dd)", example = "2025-06-15")
        String date,

        @ArraySchema(arraySchema = @Schema(description = "유저 상태 목록"))
        List<UserStatus> userStatus
) {
}
