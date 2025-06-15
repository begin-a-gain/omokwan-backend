package begin_a_gain.omokwang.match.dto.match_board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserInfo(

        @Schema(description = "유저 ID", example = "10000001")
        Long userId,

        @Schema(description = "유저 닉네임", example = "omok")
        String nickname

) {
}