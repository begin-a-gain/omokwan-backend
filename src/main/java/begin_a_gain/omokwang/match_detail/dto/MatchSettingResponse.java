package begin_a_gain.omokwang.match_detail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "대국 세팅 조회")
public class MatchSettingResponse {

    @Schema(description = "대국 이름", example = "운동 습관 만들기")
    private String name;

    @Schema(description = "대국 진행 일 수", example = "3")
    private int ongoingDays;

    @Schema(description = "대국코드", example = "IZMF54PHI8")
    private String matchCode;

    @Schema(description = "반복 요일 (1:MONDAY, 2:TUESDAY, ..., 8:WEEKDAYS, 9:WEEKENDS, 10:EVERYDAY)", example = "[1, 3, 5]")
    private List<Integer> repeatDayTypes;

    @Schema(description = "최대 인원 수", example = "5")
    private int maxParticipants;

    @Schema(description = "대국 카테고리", example = "운동")
    private String category;

}
