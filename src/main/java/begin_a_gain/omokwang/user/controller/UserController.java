package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.NicknameRequest;
import begin_a_gain.omokwang.user.dto.NicknameUpdateDto;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<String> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        Optional<String> validationError = userService.validateNickname(nicknameRequest.getNickname());

        return validationError.map(x -> ResponseEntity.badRequest().body(x))
                .orElseGet(() -> ResponseEntity.ok("Nickname is valid and available."));

    }

    @PatchMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestBody NicknameRequest nicknameRequest) {
        final long userId = SecurityUtil.getCurrentUserId();
        String nickname = nicknameRequest.getNickname();
        NicknameUpdateDto nicknameUpdateParam = new NicknameUpdateDto(userId, nickname);

        userService.updateNickname(nicknameUpdateParam);
        return ResponseEntity.ok("Nickname updated.");

    }
}
