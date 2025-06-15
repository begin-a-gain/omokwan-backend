package begin_a_gain.omokwang.match.domain;

import begin_a_gain.omokwang.match.dto.match_board.DateStatus;
import begin_a_gain.omokwang.match.dto.match_board.UserInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record MatchBoardResponse(
        List<UserInfo> users,
        List<DateStatus> dates,
        LocalDate nextCursor
) {
}
