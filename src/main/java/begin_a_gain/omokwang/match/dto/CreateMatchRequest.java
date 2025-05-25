package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "Create Match Request")
public class CreateMatchRequest {
    @Schema(description = "대국 이름", example = "책 꾸준히 읽기")
    private String name;

    @Schema(description = "요일 타입 목록 (1:MONDAY, 2:TUESDAY, ..., 8:WEEKDAYS, 9:WEEKENDS, 10:EVERYDAY)", example = "[1, 3, 5]")
    private List<Integer> dayType;

    @NotNull
    @Schema(description = "최대 참가자 수", example = "5", maximum = "5")
    private int maxParticipants;

    @Schema(description = "대국 카테고리 코드", example = "1")
    private String categoryCode;

    @Schema(description = "공개 여부", example = "true")
    private boolean isPublic;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

}
