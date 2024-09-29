package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public User info() {
        final long userId = SecurityUtil.getCurrentUserId();
        return userService.findBySocialId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }

    @GetMapping("/validations/nicknames")
    public ResponseEntity<String> checkNickname(@RequestParam String nickname) {

        if (userService.isValidNickname(nickname)) {
            return ResponseEntity.badRequest()
                    .body("Invalid nickname: Must be 2-10 characters long and cannot include special characters.");
        }

        if (userService.isNicknameTaken(nickname)) {
            return ResponseEntity.badRequest().body("Nickname already taken.");
        }

        return ResponseEntity.ok("Nickname is valid.");
    }
}
