package begin_a_gain.omokwang.match.dto.match_board;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대국 요약 정보")
public record MatchSummary(
        @Schema(description = "대국 이름", example = "수요 오목방")
        String matchName,

        @Schema(description = "최대 참가 인원", example = "8")
        int maxParticipants
) {
}
