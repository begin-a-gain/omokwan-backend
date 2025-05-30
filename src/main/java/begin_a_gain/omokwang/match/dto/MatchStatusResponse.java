package begin_a_gain.omokwang.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "대국 상태")
@RequiredArgsConstructor
public class MatchStatusResponse {
    @Schema(description = "대국 완료 여부", example = "true")
    private final boolean completed;
}
