package begin_a_gain.omokwang.match.controller;

import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.match.application.MatchListService;
import begin_a_gain.omokwang.match.dto.MatchAllRequest;
import begin_a_gain.omokwang.match.dto.MatchAllResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Match", description = "Match API")
@RestController
@RequiredArgsConstructor
public class MatchListController {

    private final MatchListService matchService;


    @Operation(summary = "전체 대국 조회", description = "전체 대국 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/matches/all")
    public ResponseEntity<CommonResponse<List<MatchAllResponse>>> findAllMatch(
            @Parameter(description = "Joinable", example = "true")
            @RequestParam(name = "joinable", required = false) Boolean joinable,
            @Parameter(description = "Category Id", example = "1")
            @RequestParam(name = "category", required = false) Long category,
            @Parameter(
                    description = "Search keyword — filters by match name, match ID, or host name",
                    example = "exercise"
            )

            @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "Page Number (start 1)", example = "1")
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @Parameter(description = "Page Size", example = "10")
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize

    ) {
        var request = MatchAllRequest.builder()
                .joinable(joinable)
                .categoryId(category)
                .search(search)
                .pageNumber(pageNumber - 1)
                .pageSize(pageSize)
                .build();
        var response = matchService.findAllMatch(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
