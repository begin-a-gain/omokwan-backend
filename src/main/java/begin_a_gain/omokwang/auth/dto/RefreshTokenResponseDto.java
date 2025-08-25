package begin_a_gain.omokwang.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "RefreshTokenResponse", description = "Token 재발급")
public class RefreshTokenResponseDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJSUzI1NiIsInR5c")
    private String accessToken;
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9")
    private String refreshToken;
}
