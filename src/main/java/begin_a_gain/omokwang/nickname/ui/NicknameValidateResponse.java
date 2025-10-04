package begin_a_gain.omokwang.nickname.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Validate Nickname")
public class NicknameValidateResponse {

    @Schema(description = "유효성 체크", example = "true")
    @JsonProperty("isValid")
    private boolean isValid;

    @Schema(description = "닉네임 중복 여부", example = "true")
    @JsonProperty("isDuplicated")
    private boolean isDuplicated;
}
