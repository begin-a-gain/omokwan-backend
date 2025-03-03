package begin_a_gain.omokwang.daeguk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "요일별 대국 조회 응답")
public class DaegukByDayResponse {

    @Schema(description = "대국 ID", example = "1")
    private Long daegukId;

    @Schema(description = "대국 이름", example = "운동 습관 만들기")
    private String name;

    @Schema(description = "진행된 일 수", example = "3")
    private int ongoingDays;

    @Schema(description = "현재 참가자 수", example = "2")
    private int participants;

    @Schema(description = "최대 참가자 수", example = "5")
    private int maxParticipants;

    @Schema(description = "대국 공개:q! 여부", example = "true")
    private boolean isPublic;

}
