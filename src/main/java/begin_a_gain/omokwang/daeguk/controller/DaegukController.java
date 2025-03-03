package begin_a_gain.omokwang.daeguk.controller;

import begin_a_gain.omokwang.daeguk.application.DaegukService;
import begin_a_gain.omokwang.daeguk.domain.Category;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukRequest;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukResponse;
import begin_a_gain.omokwang.daeguk.dto.DaegukByDayResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Daeguk", description = "Daeguk API")
@RestController
@RequiredArgsConstructor
public class DaegukController {

    private final DaegukService daegukService;

    @Operation(summary = "대국 생성", description = "대국을 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content), // 응답 본문이 없음을 암시
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @PostMapping("/daeguks")
    public CreateDaegukResponse createDaeguk(@RequestBody CreateDaegukRequest request) {
        return daegukService.createDaeguk(request);
    }

    @Operation(summary = "대국 찾기", description = "날짜별 대국 찾기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DaegukByDayResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/daeguks")
    public List<DaegukByDayResponse> findDaegukByDay(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)", example = "2025-03-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return daegukService.findDaegukByday(date);
    }

    @Operation(summary = "대국 카테고리", description = "대국 카테고리 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/daeguks/categories")
    public List<Category> getDaegukCategories() {
        return daegukService.getDaegukCategories();
    }
}
