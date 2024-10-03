package begin_a_gain.omokwang.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "OAuth request")
public class OauthRequestDto {
    @NotBlank(message = "Kakao Access Token을 입력해주세요.")
    private String accessToken;
}
