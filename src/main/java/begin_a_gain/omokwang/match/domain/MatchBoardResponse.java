package begin_a_gain.omokwang.match.domain;

import begin_a_gain.omokwang.match.dto.match_board.DateStatus;
import begin_a_gain.omokwang.match.dto.match_board.UserInfo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "대국 보드 응답 DTO")
public record MatchBoardResponse(

        @ArraySchema(arraySchema = @Schema(description = "참여한 유저 정보 리스트"))
        List<UserInfo> users,

        @ArraySchema(arraySchema = @Schema(description = "날짜별 유저 상태 리스트"))
        List<DateStatus> dates,

        @Schema(description = "다음 요청을 위한 기준 날짜")
        LocalDate nextCursor

) {
}
