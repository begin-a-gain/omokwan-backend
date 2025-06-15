package begin_a_gain.omokwang.match.dto.match_board;

import begin_a_gain.omokwang.match.domain.CompletionStatus;

public record UserStatus(
        Long userId,
        CompletionStatus status,
        Integer comboLength,  // 1, 2, 3, ..., null
        boolean isCombo
) {
}
