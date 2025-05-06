package begin_a_gain.omokwang.daeguk_detail.controller;

import begin_a_gain.omokwang.daeguk_detail.application.DaegukDetailService;
import begin_a_gain.omokwang.daeguk_detail.dto.JoinDaegukRequest;
import begin_a_gain.omokwang.daeguk_detail.dto.JoinDaegukResponse;
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

@Tag(name = "Daeguk", description = "Daeguk API")
@RestController
@RequiredArgsConstructor
public class DaegukDetailController {

    private final DaegukDetailService daegukDetailService;

    @Operation(summary = "대국 참여", description = "대국 참여.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @PostMapping("/daeguks/{daegukId}/participants")
    public ResponseEntity<JoinDaegukResponse> joinDaeguk(
            @PathVariable Long daegukId,
            @RequestBody JoinDaegukRequest request) {
        daegukDetailService.joinDaeguk(daegukId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
