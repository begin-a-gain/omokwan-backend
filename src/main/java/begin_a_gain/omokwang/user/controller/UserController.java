package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user info", description = "Fetches user information based on the current user's ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/info")
    public ResponseEntity<CommonResponse<User>> info() {
        final long userId = SecurityUtil.getCurrentUserSocialId();
        User user = userService.findBySocialId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        return ResponseEntity.ok(CommonResponse.success(user));
    }

    @Operation(
            summary = "Delete user account",
            description = "Deletes the currently authenticated user's account based on their ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User account successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public void deleteUser() {
        long socialId = SecurityUtil.getCurrentUserSocialId();
        userService.deleteUser(socialId);
    }
}
