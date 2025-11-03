package begin_a_gain.omokwang.match.controller;

import begin_a_gain.omokwang.match.dto.MatchAllResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "전체 대국 리스트 응답")
public class MatchAllControllerResponse {

    @Schema(description = "대국 리스트")
    private List<MatchAllResponse> matchList;
    
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

}

