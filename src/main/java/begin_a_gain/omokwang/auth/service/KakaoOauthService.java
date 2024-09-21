package begin_a_gain.omokwang.auth.service;

import begin_a_gain.omokwang.auth.dto.KakaoInfoDto;
import begin_a_gain.omokwang.user.dto.UserDto;
import begin_a_gain.omokwang.user.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class KakaoOauthService {
    private final UserService userService;

    // 카카오Api 호출해서 AccessToken으로 유저정보 가져오기
    public Map<String, Object> getUserAttributesByToken(String accessToken) {
        return WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    // 카카오API에서 가져온 유저정보를 DB에 저장
    public UserDto getUserProfileByToken(String accessToken) {
        Map<String, Object> userAttributesByToken = getUserAttributesByToken(accessToken);
        KakaoInfoDto kakaoInfoDto = new KakaoInfoDto(userAttributesByToken);
        UserDto userDto = UserDto.builder()
                .id(kakaoInfoDto.getId())
                .email(kakaoInfoDto.getEmail())
                .platform("kakao")
                .build();

        userService.save(userDto);
        return userDto;
    }
}
