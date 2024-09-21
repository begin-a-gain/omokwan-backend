package begin_a_gain.omokwang.user.controller;

import begin_a_gain.omokwang.user.dto.UserDto;
import begin_a_gain.omokwang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public UserDto info() {
        final long userId = SecurityUtil.getCurrentUserId();
        UserDto userDto = userService.findById(userId);
        if (userDto == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        return userDto;
    }
}
