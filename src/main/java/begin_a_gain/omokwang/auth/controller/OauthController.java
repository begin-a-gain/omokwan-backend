package begin_a_gain.omokwang.auth.controller;

import begin_a_gain.omokwang.auth.dto.AppleLoginRequestDto;
import begin_a_gain.omokwang.auth.dto.KakaoLoginRequestDto;
import begin_a_gain.omokwang.auth.dto.OauthDto;
import begin_a_gain.omokwang.auth.dto.OauthResponseDto;
import begin_a_gain.omokwang.auth.dto.RefreshTokenResponseDto;
import begin_a_gain.omokwang.auth.service.OauthService;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
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
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Auth 관련 API")
@RestController
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;
    private final UserService userService;

    @Operation(summary = "Kakao 로그인", description = "Kakao Access Token으로 로그인을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/auth/login/kakao")
    public ResponseEntity<CommonResponse<OauthResponseDto>> loginWithKakao(
            @Valid @RequestBody KakaoLoginRequestDto request,
            HttpServletResponse response) {
        OauthDto oauthInfo = oauthService.loginWithKakao(request.getAccessToken(), response);
        boolean signUpComplete = userService.isSignUpComplete(oauthInfo.getUserId());
        var oauthResponseDto = new OauthResponseDto(oauthInfo.getAccessToken(), signUpComplete);
        return ResponseEntity.ok(CommonResponse.success(oauthResponseDto));
    }

    @Operation(summary = "Apple 로그인", description = "Apple Identity Token으로 로그인을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/auth/login/apple")
    public ResponseEntity<CommonResponse<OauthResponseDto>> loginWithApple(
            @Valid @RequestBody AppleLoginRequestDto request,
            HttpServletResponse response) {
        OauthDto oauthInfo = oauthService.loginWithApple(request.getIdentityToken(), response);
        boolean signUpComplete = userService.isSignUpComplete(oauthInfo.getUserId());
        var oauthResponseDto = new OauthResponseDto(oauthInfo.getAccessToken(), signUpComplete);
        return ResponseEntity.ok(CommonResponse.success(oauthResponseDto));
    }

    @Operation(summary = "Access Token 재발급", description = "리프레시 토큰과 액세스 토큰을 재발급한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지않은 리프레시 토큰입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponseDto>> tokenRefresh(HttpServletRequest request) {
        Cookie[] list = request.getCookies();
        if (list == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Cookie refreshTokenCookie = Arrays.stream(list)
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        var refreshResponse = oauthService.refreshToken(refreshTokenCookie.getValue());
        return ResponseEntity.ok(CommonResponse.success(refreshResponse));
    }
}
