package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Create Match Response")
public class CreateMatchResponse {
    @Schema(description = "대국 아이디", example = "1")
    private Long matchId;
}
