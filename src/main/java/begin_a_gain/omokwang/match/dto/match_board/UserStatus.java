package begin_a_gain.omokwang.match.dto.match_board;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserStatus(
        @Schema(description = "유저 ID", example = "10000001")
        Long userId,

        @Schema(description = "완료)", example = "true")
        boolean isCompleted,

        @Schema(description = "연속 완료 횟수", example = "3")
        Integer streakCount,

        @Schema(description = "콤보 여부 (콤보 달성 중인지 여부)", example = "true")
        boolean isCombo
) {
}
