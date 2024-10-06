package begin_a_gain.omokwang.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthDto {
    private String accessToken;
    private long socialId;
}

