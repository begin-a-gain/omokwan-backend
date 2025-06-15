package begin_a_gain.omokwang.match.dto.match_board;

import java.util.List;

public record DateStatus(
        String date,
        List<UserStatus> userStatus
) {
}
