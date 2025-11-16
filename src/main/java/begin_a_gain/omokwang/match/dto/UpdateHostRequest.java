package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Update Host Request")
public class UpdateHostRequest {
    @Schema(description = "새 방장 User ID", example = "3")
    private Long userId;
}
