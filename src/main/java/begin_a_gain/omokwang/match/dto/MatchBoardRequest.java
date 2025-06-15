package begin_a_gain.omokwang.match.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchBoardRequest {
    private Long matchId;
    private LocalDate date;
    private int pageSize;
}
