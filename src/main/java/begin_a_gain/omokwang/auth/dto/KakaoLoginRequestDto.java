package begin_a_gain.omokwang.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Kakao OAuth login request")
public class KakaoLoginRequestDto {

    @NotBlank(message = "Kakao Access Token 입력해주세요.")
    @Schema(description = "카카오 Access Token")
    private String accessToken;
}
