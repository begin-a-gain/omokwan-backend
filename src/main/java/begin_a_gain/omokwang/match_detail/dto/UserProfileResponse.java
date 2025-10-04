package begin_a_gain.omokwang.match_detail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Get User Profile By Match Response")
public class UserProfileResponse {
    @Schema(description = "닉네임", example = "오목완")
    private String nickName;

    @Schema(description = "오목 콤보", example = "5")
    private int combo;

    @Schema(description = "오목알 개수", example = "2")
    private int participantNumbers;

    @Schema(description = "참여일 수", example = "7")
    private long participantDays;

    @JsonProperty("isHost")
    @Schema(description = "뱡장 여부", example = "true")
    private boolean host;

}
