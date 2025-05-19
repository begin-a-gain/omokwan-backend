package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Match Status Request")
public class MatchStatusRequest {
    @Schema(description = "대국 ID", example = "1")
    private Long matchId;

    @Schema(description = "대국 완료", example = "true")
    private boolean completed;

}
