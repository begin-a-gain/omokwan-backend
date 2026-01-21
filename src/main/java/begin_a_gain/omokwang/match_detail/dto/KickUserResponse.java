package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Kick User Request")
public class KickUserResponse {
    @Schema(description = "추방된 사용자", example = "1234")
    private Long userId;

}
