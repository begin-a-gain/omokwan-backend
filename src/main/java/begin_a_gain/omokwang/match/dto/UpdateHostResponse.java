package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Update Host Response")
public class UpdateHostResponse {
    @Schema(description = "방장 아이디", example = "1")
    private Long hostId;
}
