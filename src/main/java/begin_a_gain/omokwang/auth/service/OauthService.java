package begin_a_gain.omokwang.auth.service;

import begin_a_gain.omokwang.auth.dto.OauthDto;
import begin_a_gain.omokwang.auth.dto.RefreshTokenResponseDto;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OauthService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final KakaoOauthService kakaoOauthService;
    private final AppleOauthService appleOauthService;

    //카카오 로그인
    public OauthDto loginWithKakao(String accessToken, HttpServletResponse response) {
        User user = kakaoOauthService.getUserProfileByToken(accessToken);
        return new OauthDto(getTokens(user, response), user.getId());
    }

    public OauthDto loginWithApple(String identityToken, HttpServletResponse response) {
        User user = appleOauthService.getUserProfileByIdentityToken(identityToken);
        return new OauthDto(getTokens(user, response), user.getId());
    }

    public String getTokens(User user, HttpServletResponse response) {
        String accessToken = jwtTokenService.createAccessToken(user.getId().toString());
        String refreshToken = jwtTokenService.createRefreshToken();
        userService.updateRefreshToken(user.getId(), refreshToken);

        jwtTokenService.addRefreshTokenToCookie(refreshToken, response);
        return accessToken;
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(String refreshToken) {
        User user = Optional.ofNullable(userService.findByRefreshToken(refreshToken))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!jwtTokenService.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        var accessToken = getAccessToken(user);
        var newRefreshToken = jwtTokenService.createRefreshToken();
        userService.updateRefreshToken(user.getId(), newRefreshToken);

        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    private String getAccessToken(User user) {
        return jwtTokenService.createAccessToken(user.getId().toString());
    }

}
