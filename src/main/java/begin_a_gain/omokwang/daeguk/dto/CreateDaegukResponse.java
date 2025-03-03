package begin_a_gain.omokwang.daeguk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Create Daeguk Response")
public class CreateDaegukResponse {
    @Schema(description = "대국 아이디", example = "1")
    private Long daegukId;
}
