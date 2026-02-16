package begin_a_gain.omokwang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "마이페이지 응답")
public class MyPageResponse {

    @Schema(description = "유저 ID", example = "10")
    private Long userId;

    @Schema(description = "닉네임", example = "오목완짱")
    private String nickname;

    @Schema(description = "진행 중 대국 수", example = "13")
    private long inProgressMatchCount;

    @Schema(description = "완료 대국 수", example = "5")
    private long completedMatchCount;

    @Schema(description = "진행 중 대국 목록")
    private List<MyPageMatchSummaryResponse> inProgressMatches;

    @Schema(description = "완료 대국 목록")
    private List<MyPageMatchSummaryResponse> completedMatches;
}
