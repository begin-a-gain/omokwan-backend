package begin_a_gain.omokwang.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "마이페이지 대국 요약")
public class MyPageMatchSummaryResponse {

    @Schema(description = "대국 ID", example = "1")
    private Long matchId;

    @Schema(description = "대국 이름", example = "30분 이상 아침 달리기 하기")
    private String matchName;

    @Schema(description = "대국 참여일 수", example = "12")
    private int participantDays;

    @Schema(description = "콤보 수", example = "3")
    private int comboCount;

    @Schema(description = "오목알 수", example = "12")
    private int participantNumbers;

    @Schema(description = "대국 요일 목록(월:1 ~ 일:7)", example = "[1,3,5]")
    private List<Integer> dayOfWeeks;
}
