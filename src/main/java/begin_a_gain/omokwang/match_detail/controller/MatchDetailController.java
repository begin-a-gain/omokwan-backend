package begin_a_gain.omokwang.match_detail.controller;

import begin_a_gain.omokwang.match_detail.application.MatchDetailService;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchRequest;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Match", description = "Match API")
@RestController
@RequiredArgsConstructor
public class MatchDetailController {

    private final MatchDetailService matchDetailService;

    @Operation(summary = "대국 참여", description = "대국 참여.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @PostMapping("/matches/{matchId}/participants")
    public ResponseEntity<JoinMatchResponse> joinMatch(
            @PathVariable Long matchId,
            @RequestBody JoinMatchRequest request) {
        matchDetailService.joinMatch(matchId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
