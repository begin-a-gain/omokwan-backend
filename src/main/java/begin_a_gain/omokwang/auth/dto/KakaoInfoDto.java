package begin_a_gain.omokwang.auth.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoInfoDto {
    private Long id;
    private String email;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.id = Long.valueOf(attributes.get("id").toString());
        this.email = attributes.get("email") != null ? attributes.get("email").toString() : "";
    }
}
