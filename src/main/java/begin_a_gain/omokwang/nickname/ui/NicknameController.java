package begin_a_gain.omokwang.nickname.ui;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.nickname.application.NicknameService;
import begin_a_gain.omokwang.nickname.domain.NicknameUpdateDto;
import begin_a_gain.omokwang.nickname.dto.NicknameRequest;
import begin_a_gain.omokwang.nickname.dto.NicknameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nickname", description = "Nickname API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class NicknameController {
    private final NicknameService nicknameService;

    @Operation(summary = "Update user nickname", description = "nickname 업데이트.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname updated successfully")
    })
    @PutMapping("/nicknames")
    public ResponseEntity<String> updateNickname(@RequestBody NicknameRequest nicknameRequest) {
        final long userId = SecurityUtil.getCurrentUserSocialId();
        String nickname = nicknameRequest.getNickname();
        NicknameUpdateDto nicknameUpdateParam = new NicknameUpdateDto(userId, nickname);

        nicknameService.updateNickname(nicknameUpdateParam);
        return ResponseEntity.ok("Nickname updated.");

    }

    @Operation(summary = "Check nickname availability", description = "Checks if the provided nickname is valid and available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname is valid and available",
                    content = @Content(schema = @Schema(implementation = NicknameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid nickname or nickname already taken", content = @Content)
    })
    @PostMapping("/nicknames/validations")
    public ResponseEntity<String> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        Optional<String> validationError = nicknameService.validateNickname(nicknameRequest.getNickname());

        return validationError.map(x -> ResponseEntity.badRequest().body(x))
                .orElseGet(() -> ResponseEntity.ok("Nickname is valid and available."));
    }
}
