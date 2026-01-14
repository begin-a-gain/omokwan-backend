package begin_a_gain.omokwang.match_detail.controller;

import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.match_detail.application.MatchSettingService;
import begin_a_gain.omokwang.match_detail.dto.MatchSettingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Match", description = "Match API")
@RestController
@RequiredArgsConstructor
public class MatchSettingController {

    private final MatchSettingService matchSettingService;

    @Operation(summary = "대국 세팅", description = "대국 세팅 조회.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches/{matchId}/settings")
    public ResponseEntity<CommonResponse<MatchSettingResponse>> matchSetting(
            @PathVariable("matchId") Long matchId) {
        var response = matchSettingService.getSettingMatch(matchId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}