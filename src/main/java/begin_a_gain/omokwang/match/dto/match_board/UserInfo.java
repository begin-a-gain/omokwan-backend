package begin_a_gain.omokwang.match.dto.match_board;

import lombok.Builder;

@Builder
public record UserInfo(
        Long userId,
        String nickname
) {
}