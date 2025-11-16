package begin_a_gain.omokwang.match.controller;

import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.match.application.MatchService;
import begin_a_gain.omokwang.match.domain.Category;
import begin_a_gain.omokwang.match.domain.MatchBoardResponse;
import begin_a_gain.omokwang.match.dto.CreateMatchRequest;
import begin_a_gain.omokwang.match.dto.CreateMatchResponse;
import begin_a_gain.omokwang.match.dto.MatchBoardRequest;
import begin_a_gain.omokwang.match.dto.MatchByDayResponse;
import begin_a_gain.omokwang.match.dto.MatchStatusResponse;
import begin_a_gain.omokwang.match.dto.UpdateHostRequest;
import begin_a_gain.omokwang.match.dto.UpdateHostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Match", description = "Match API")
@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "대국 생성", description = "대국을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/matches")
    public ResponseEntity<CommonResponse<CreateMatchResponse>> createMatch(@RequestBody CreateMatchRequest request) {
        var response = matchService.createMatch(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국장 변경", description = "대국장을 변경한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PutMapping("/matches/{matchId}/host")
    public ResponseEntity<CommonResponse<UpdateHostResponse>> updateHost(
            @PathVariable("matchId")
            @Schema(example = "1")
            Long matchId,
            @RequestBody UpdateHostRequest request) {
        var response = matchService.updateHost(matchId, request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "내 대국 찾기", description = "날짜별 내 대국 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches")
    public ResponseEntity<CommonResponse<List<MatchByDayResponse>>> findMatchByDay(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-03-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var response = matchService.findMatchByDay(date);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국 카테고리", description = "대국 카테고리 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches/categories")
    public ResponseEntity<CommonResponse<List<Category>>> getMatchCategories() {
        var response = matchService.getMatchCategories();
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국 완료", description = "대국 상태를 완료 or 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/matches/{matchId}/status")
    public ResponseEntity<CommonResponse<MatchStatusResponse>> matchStatus(
            @PathVariable("matchId")
            @Schema(example = "1")
            Long matchId,
            @Parameter(description = "대국 상태 입력 날짜 (YYYY-MM-DD 형식)", example = "2025-03-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var response = matchService.matchStatus(date, matchId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대국 메인 보드", description = "대국 메인 보드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches/{matchId}/board")
    public ResponseEntity<CommonResponse<MatchBoardResponse>> getMatchBoard(
            @PathVariable("matchId") @Schema(example = "1") Long matchId,
            @Parameter(description = "(YYYY-MM-DD)", example = "2025-09-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Page Size", example = "2")
            @RequestParam("size") int size
    ) {
        var request = convertToRequest(matchId, from, size);
        var response = matchService.getBoardForMatch(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    private static MatchBoardRequest convertToRequest(Long matchId, LocalDate date, int size) {
        return MatchBoardRequest.builder()
                .matchId(matchId)
                .date(date)
                .pageSize(size)
                .build();
    }
}
