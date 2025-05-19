package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Join Match Response")
public class JoinMatchResponse {
    @Schema(description = "대국 아이디", example = "1")
    private Long matchId;
}
