package begin_a_gain.omokwang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "유저 목록 조회 응답")
public record UserListResponse(
        @Schema(description = "유저 목록")
        List<UserSummaryResponse> users,
        @Schema(description = "다음 조회 커서", example = "가나다::10")
        String nextCursor,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
}
