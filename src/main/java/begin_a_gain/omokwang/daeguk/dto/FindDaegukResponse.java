package begin_a_gain.omokwang.daeguk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Create Daeguk Request")
public class FindDaegukResponse {
    @Schema(description = "대국 이름", example = "책 꾸준히 읽기")
    private String name;

    @Schema(description = "대국이 생성된 이후 경과된 일수", example = "10")
    private int gameDays;

    @Schema(description = "최대 참가자 수", example = "5", maximum = "5")
    private int maxParticipants;

    @Schema(description = "참가자 수", example = "3", maximum = "5")
    private int participants;

    @Schema(description = "대국 완료 여부", example = "true|false")
    private boolean isCompleted;

}
