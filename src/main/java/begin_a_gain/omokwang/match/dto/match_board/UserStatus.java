package begin_a_gain.omokwang.match.dto.match_board;

import begin_a_gain.omokwang.match.domain.CompletionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserStatus(
        @Schema(description = "유저 ID", example = "10000001")
        Long userId,

        @Schema(description = "참여 상태 (COMPLETED/NOT_COMPLETED)", example = "COMPLETED")
        CompletionStatus status,

        @Schema(description = "콤보 길이", example = "3")
        Integer comboLength,

        @Schema(description = "콤보 여부 (콤보 달성 중인지 여부)", example = "true")
        boolean isCombo
) {
}
