package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "대국 초대 요청")
public record InviteUsersRequest(
        @Schema(description = "초대할 유저 ID 목록", example = "[2, 3, 4]")
        List<Long> userIds
) {
}
