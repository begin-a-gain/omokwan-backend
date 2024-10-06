package begin_a_gain.omokwang.auth.controller;

import begin_a_gain.omokwang.auth.dto.OauthDto;
import begin_a_gain.omokwang.auth.dto.OauthRequestDto;
import begin_a_gain.omokwang.auth.dto.OauthResponseDto;
import begin_a_gain.omokwang.auth.dto.RefreshTokenResponseDto;
import begin_a_gain.omokwang.auth.service.OauthService;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Oauth", description = "Auth 관련 API")
@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;
    private final UserService userService;
    // 생성자 주입 - 테스트 편의성, 불변 보장, 의존성 명확성

    @Operation(summary = "Oauth 로그인", description = "Oauth 로그인을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = OauthRequestDto.class))}),
    })
    @PostMapping("/login/oauth/{provider}")
    public OauthResponseDto login(@PathVariable("provider") String provider,
                                  @RequestBody OauthRequestDto oauthRequestDto,
                                  HttpServletResponse response) {
        OauthResponseDto oauthResponseDto = new OauthResponseDto();
        switch (provider) {
            case "kakao":
                OauthDto oauthInfo = oauthService.loginWithKakao(oauthRequestDto.getAccessToken(), response);

                boolean isSignUpComplete = userService.isSignUpComplete(oauthInfo.getSocialId());
                oauthResponseDto = new OauthResponseDto(oauthInfo.getAccessToken(), isSignUpComplete);
        }
        return oauthResponseDto;
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @Operation(summary = "Access Token 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지않은 리프레시 토큰입니다.", content = @Content)
    })
    @PostMapping("/token/refresh")
    public RefreshTokenResponseDto tokenRefresh(HttpServletRequest request) {
        RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
        Cookie[] list = request.getCookies();
        if (list == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Cookie refreshTokenCookie = Arrays.stream(list).filter(cookie -> cookie.getName().equals("refresh_token"))
                .collect(Collectors.toList()).get(0);

        if (refreshTokenCookie == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        String accessToken = oauthService.refreshAccessToken(refreshTokenCookie.getValue());
        refreshTokenResponseDto.setAccessToken(accessToken);
        return refreshTokenResponseDto;
    }
}