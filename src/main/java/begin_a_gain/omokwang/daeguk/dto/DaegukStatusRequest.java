package begin_a_gain.omokwang.daeguk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Daeguk Status Request")
public class DaegukStatusRequest {
    @Schema(description = "대국 ID", example = "1")
    private Long daegukId;

    @Schema(description = "대국 완료", example = "true")
    private boolean isCompleted;

}
