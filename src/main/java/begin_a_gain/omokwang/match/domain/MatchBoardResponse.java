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

        @Schema(description = "과거 데이터 요청을 위한 날짜", example = "2025-08-30")
        LocalDate prevCursor,

        @Schema(description = "미래 데이터 요청을 위한 기준 날짜", example = "2025-09-03")
        LocalDate nextCursor,

        @Schema(description = "과거 데이터 조회 가능 여부", example = "true")
        boolean hasPrev,

        @Schema(description = "미래 데이터 조회 가능 여부", example = "true")
        boolean hasNext,
        @Schema(description = "오늘 대국 완료 여부", example = "true")
        boolean isTodayMatchCompleted
) {
}
