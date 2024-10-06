package begin_a_gain.omokwang.nickname.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NicknameValidationResponse {
    private boolean validNickname;
    private boolean isSignupComplete;
}