package begin_a_gain.omokwang.daeguk.controller;

import begin_a_gain.omokwang.daeguk.application.DaegukService;
import begin_a_gain.omokwang.daeguk.domain.Category;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukRequest;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukResponse;
import begin_a_gain.omokwang.daeguk.dto.DaegukByDayResponse;
import begin_a_gain.omokwang.daeguk.dto.DaegukStatusRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<CreateDaegukResponse> createDaeguk(@RequestBody CreateDaegukRequest request) {
        var response = daegukService.createDaeguk(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "대국 찾기", description = "날짜별 대국 찾기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DaegukByDayResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/daeguks")
    public ResponseEntity<List<DaegukByDayResponse>> findDaegukByDay(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)", example = "2025-03-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var response = daegukService.findDaegukByday(date);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "대국 카테고리", description = "대국 카테고리 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping("/daeguks/categories")
    public ResponseEntity<List<Category>> getDaegukCategories() {
        var response = daegukService.getDaegukCategories();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "대국 완료", description = "대국 상태를 완료 or 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @PutMapping("/daeguks/status")
    public void daegukStatus(
            @Parameter(description = "대국 상태 입력 날짜 (YYYY-MM-DD 형식)", example = "2025-03-01")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody DaegukStatusRequest request) {
        var response = daegukService.daegukStatus(date, request);
        return ResponseEntity.status(HttpStatus.OK);
    }
}
