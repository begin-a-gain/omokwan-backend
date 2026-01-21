package begin_a_gain.omokwang.match_detail.controller;

import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.match_detail.application.MatchDetailService;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchRequest;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchResponse;
import begin_a_gain.omokwang.match_detail.dto.KickUserResponse;
import begin_a_gain.omokwang.match_detail.dto.LeaveMatchResponse;
import begin_a_gain.omokwang.match_detail.dto.MatchParticipantsResponse;
import begin_a_gain.omokwang.match_detail.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
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
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/matches/{matchId}/participants")
    public ResponseEntity<CommonResponse<JoinMatchResponse>> joinMatch(
            @PathVariable("matchId") Long matchId,
            @Nullable @RequestBody JoinMatchRequest request) {
        var response = matchDetailService.joinMatch(matchId, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국 참여자 조회", description = "대국 참여자 조회.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches/{matchId}/participants")
    public ResponseEntity<CommonResponse<MatchParticipantsResponse>> getParticipants(
            @PathVariable("matchId") Long matchId) {
        var response = matchDetailService.getParticipants(matchId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국별 유저 프로필", description = "대국별 유저 프로필.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
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

    @Operation(summary = "유저 추방하기", description = "대국별 유저 추방하기.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/matches/{matchId}/users/{userId}/kick")
    public ResponseEntity<CommonResponse<KickUserResponse>> kickUser(
            @Parameter(description = "Unique ID of the match", example = "100")
            @PathVariable("matchId") long matchId,
            @Parameter(description = "Unique ID of the user", example = "200")
            @PathVariable("userId") long userId) {
        var response = matchDetailService.kickUserFromMatch(matchId, userId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국 나가기", description = "대국 나가기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/matches/{matchId}/participants/me")
    public ResponseEntity<CommonResponse<LeaveMatchResponse>> leaveMatch(
            @Parameter(description = "Unique ID of the match", example = "100")
            @PathVariable("matchId") long matchId) {
        var response = matchDetailService.leaveMatch(matchId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
