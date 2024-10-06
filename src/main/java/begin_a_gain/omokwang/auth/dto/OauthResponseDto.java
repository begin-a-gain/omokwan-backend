package begin_a_gain.omokwang.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthResponseDto {
    private String accessToken;
    private boolean signUpComplete;
}

