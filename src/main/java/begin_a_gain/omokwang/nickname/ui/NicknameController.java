package begin_a_gain.omokwang.nickname.ui;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.nickname.application.NicknameService;
import begin_a_gain.omokwang.nickname.domain.NicknameUpdateDto;
import begin_a_gain.omokwang.nickname.dto.NicknameRequest;
import begin_a_gain.omokwang.nickname.dto.NicknameValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nickname", description = "Nickname API")
@RestController
@RequiredArgsConstructor
public class NicknameController {
    private final NicknameService nicknameService;

    @Operation(summary = "Check nickname availability", description = "Checks if the provided nickname is valid and available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname is valid and available",
                    content = @Content(schema = @Schema(implementation = NicknameValidationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid nickname or nickname already taken", content = @Content)
    })
    @PostMapping("/validations/nicknames")
    public ResponseEntity<?> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        Optional<String> validationError = nicknameService.validateNickname(nicknameRequest.getNickname());

        final long socialId = SecurityUtil.getCurrentUserId();
        boolean isSignUpComplete = nicknameService.isSignUpComplete(socialId);
        return validationError
                .map(error -> ResponseEntity.badRequest().body((Object) error))  // 명시적으로 Object로 캐스팅
                .orElseGet(() -> {
                    NicknameValidationResponse response = new NicknameValidationResponse(true,
                            isSignUpComplete);
                    return ResponseEntity.ok(response);
                });
    }

    @Operation(summary = "Update user nickname", description = "nickname 업데이트.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname updated successfully")
    })
    @PatchMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestBody NicknameRequest nicknameRequest) {
        final long userId = SecurityUtil.getCurrentUserId();
        String nickname = nicknameRequest.getNickname();
        NicknameUpdateDto nicknameUpdateParam = new NicknameUpdateDto(userId, nickname);

        nicknameService.updateNickname(nicknameUpdateParam);
        return ResponseEntity.ok("Nickname updated.");

    }
}
