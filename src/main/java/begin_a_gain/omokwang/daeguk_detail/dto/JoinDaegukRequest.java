package begin_a_gain.omokwang.daeguk_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Join Daeguk Request")
public class JoinDaegukRequest {
    @Schema(description = "대국 비밀번호", example = "1234")
    private String password;
}
