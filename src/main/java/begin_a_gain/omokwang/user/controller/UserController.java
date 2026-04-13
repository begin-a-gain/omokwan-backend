package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.user.dto.DeletionSurveyRequest;
import begin_a_gain.omokwang.user.dto.MyPageResponse;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.dto.UserListResponse;
import begin_a_gain.omokwang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/info")
    public ResponseEntity<CommonResponse<User>> info() {
        final long userId = SecurityUtil.getCurrentUserId();
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        return ResponseEntity.ok(CommonResponse.success(user));
    }

    @Operation(summary = "유저 목록 조회", description = "닉네임 검색 + cursor 기반 무한 스크롤")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<CommonResponse<UserListResponse>> getUsers(
            @RequestParam(value = "matchId") Long matchId,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        var response = userService.getUsersByNickname(matchId, nickname, cursor, size);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "My page", description = "My page by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{userId}/mypage")
    public ResponseEntity<CommonResponse<MyPageResponse>> myPage(
            @PathVariable("userId") Long userId) {
        var response = userService.getMyPage(userId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(
            summary = "Delete user account",
            description = "Deletes the currently authenticated user's account based on their ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User account successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public void deleteUser() {
        long userId = SecurityUtil.getCurrentUserId();
        userService.deleteUser(userId);
    }

    @Operation(
            summary = "Deletion survey",
            description = "Deletion survey information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/me/deletion-survey")
    public ResponseEntity<CommonResponse<String>> deletionSurvey(
            @Valid @Nullable @RequestBody DeletionSurveyRequest request
    ) {
        userService.deletionSurvey(request);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
