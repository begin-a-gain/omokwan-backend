package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantInfo {
    @Schema(description = "User ID", example = "1")
    Long userId;

    @Schema(description = "닉네임", example = "오목완짱")
    String nickname;

    @Schema(description = "오목 콤보", example = "5")
    private int combo;

    @Schema(description = "참여일 수", example = "7")
    private long participantDays;

}