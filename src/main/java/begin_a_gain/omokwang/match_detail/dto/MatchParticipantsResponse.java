package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Get Match Participants Response")
public class MatchParticipantsResponse {
    @Schema(description = "대국 참여자")
    private List<ParticipantInfo> userInfo;

}