package begin_a_gain.omokwang.daeguk.controller;

import begin_a_gain.omokwang.daeguk.application.DaegukService;
import begin_a_gain.omokwang.daeguk.dto.CreateDaeGukRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Daeguk", description = "Daeguk 관련 API")
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
    public void createDaeguk(@RequestBody CreateDaeGukRequest request) {
        daegukService.createDaeguk(request);
    }

    @GetMapping("/daeguks")
    public void findDaeguk(@RequestBody CreateDaeGukRequest request) {
        daegukService.createDaeguk(request);
    }
}
