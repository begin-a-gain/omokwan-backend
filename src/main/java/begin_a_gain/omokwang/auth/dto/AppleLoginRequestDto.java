package begin_a_gain.omokwang.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Apple OAuth login request")
public class AppleLoginRequestDto {

    @NotBlank(message = "Apple Identity Token을 입력해주세요.")
    @Schema(description = "Apple Identity Token (JWT)")
    private String identityToken;
}
