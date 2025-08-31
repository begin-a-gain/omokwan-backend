package begin_a_gain.omokwang.match_detail.controller;

import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.match_detail.application.MatchDetailService;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchRequest;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchResponse;
import begin_a_gain.omokwang.match_detail.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
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
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/matches/{matchId}/participants")
    public ResponseEntity<CommonResponse<JoinMatchResponse>> joinMatch(
            @PathVariable("matchId") Long matchId,
            @Nullable @RequestBody JoinMatchRequest request) {
        matchDetailService.joinMatch(matchId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "대국별 유저 프로필", description = "유저 프로필.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/matches/{matchId}/users/{userId}")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getUserProfileByMatchId(
            @Parameter(description = "Unique ID of the match", example = "100")
            @PathVariable("matchId") long matchId,
            @Parameter(description = "Unique ID of the user", example = "200")
            @PathVariable("userId") long userId) {
        var response = matchDetailService.getUserProfileByMatchId(matchId, userId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
