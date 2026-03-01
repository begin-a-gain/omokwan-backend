package begin_a_gain.omokwang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 요약 정보")
public record UserSummaryResponse(
        @Schema(description = "유저 ID", example = "1")
        Long userId,
        @Schema(description = "닉네임", example = "omokwang")
        String nickname
) {
}
