package begin_a_gain.omokwang.match_detail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대국 세팅 수정 요청")
public record MatchSettingUpdateRequest(
        @Schema(description = "대국 이름", example = "운동 습관 만들기")
        String name,
        @Schema(description = "최대 인원 수", example = "5")
        int maxParticipants,
        @Schema(description = "대국 카테고리", example = "1")
        String category,
        @JsonProperty("isPublic")
        @Schema(description = "공개 여부", example = "true")
        boolean isPublic,
        @Schema(description = "비밀번호 (비공개 전환 시 필수)", example = "1234")
        String password
) {
}
