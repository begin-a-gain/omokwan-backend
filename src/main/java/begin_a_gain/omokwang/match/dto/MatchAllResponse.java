package begin_a_gain.omokwang.match.dto;

import begin_a_gain.omokwang.match.domain.JoinableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "전체 대국 리스트 응답")
public class MatchAllResponse {

    @Schema(description = "대국 ID", example = "1")
    private Long matchId;

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "대국 이름", example = "운동 습관 만들기")
    private String name;

    @Schema(description = "대국 호스트 이름", example = "omok")
    private String hostName;

    @Schema(description = "진행된 일 수", example = "3")
    private int ongoingDays;

    @Schema(description = "현재 참가자 수", example = "2")
    private int participants;

    @Schema(description = "최대 참가자 수", example = "5")
    private int maxParticipants;

    @Schema(description = "대국 공개 여부", example = "true")
    private boolean isPublic;

    @Schema(description = "대국 참여 여부", example = "JOINABLE")
    private JoinableStatus joinable;

}
