package begin_a_gain.omokwang.auth.dto;

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoInfoDto {
    private Long id;
    private String email;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.id = Long.valueOf(attributes.get("id").toString());
        this.email = Optional.ofNullable((Map<String, Object>) attributes.get("kakao_account"))
                .map(kakaoAccount -> (String) kakaoAccount.get("email"))
                .orElse("");
    }
}
