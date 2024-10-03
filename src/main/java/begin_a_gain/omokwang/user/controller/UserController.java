package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.NicknameRequest;
import begin_a_gain.omokwang.user.dto.NicknameUpdateDto;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user info", description = "Fetches user information based on the current user's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/info")
    public User info() {
        final long userId = SecurityUtil.getCurrentUserId();
        return userService.findBySocialId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }

    @Operation(summary = "Check nickname availability", description = "Checks if the provided nickname is valid and available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname is valid and available",
                    content = @Content(schema = @Schema(implementation = NicknameRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid nickname or nickname already taken", content = @Content)
    })
    @GetMapping("/validations/nicknames")
    public ResponseEntity<String> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        Optional<String> validationError = userService.validateNickname(nicknameRequest.getNickname());

        return validationError.map(x -> ResponseEntity.badRequest().body(x))
                .orElseGet(() -> ResponseEntity.ok("Nickname is valid and available."));

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

        userService.updateNickname(nicknameUpdateParam);
        return ResponseEntity.ok("Nickname updated.");

    }
}
