package begin_a_gain.omokwang.daeguk_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Join Daeguk Response")
public class JoinDaegukResponse {
    @Schema(description = "대국 아이디", example = "1")
    private Long daegukId;
}
