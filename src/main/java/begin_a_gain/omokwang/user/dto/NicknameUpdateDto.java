package begin_a_gain.omokwang.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameUpdateDto {
    private long socialId;
    private String nickname;
}