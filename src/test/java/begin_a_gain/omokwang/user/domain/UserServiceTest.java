package begin_a_gain.omokwang.user.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.user.repository.UserRepository;
import begin_a_gain.omokwang.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("User 관련 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("닉네임 중복 체크 - 닉네임이 존재하는 경우")
    void testIsNicknameTaken_NicknameExists() {
        String existingNickname = "existingNick";
        when(userRepository.existsByNickname(existingNickname)).thenReturn(true);

        boolean isTaken = userService.isNicknameTaken(existingNickname);

        assertThat(isTaken).isTrue();
    }

    @ParameterizedTest(name = "닉네임: {0}, 기대 결과: {1}")
    @CsvSource({
            "valid123, false",        // 유효한 닉네임
            "가나다123, false",        // 한글과 숫자가 포함된 유효한 닉네임
            "invalid!!, true",       // 특수문자가 포함된 닉네임
            "a, true",               // 1글자 닉네임 (유효하지 않음)
            "thisisaverylongname, true", // 10글자 초과 닉네임 (유효하지 않음)
            "nick, false",            // 4글자로 유효한 닉네임
            "닉네임test, false",       // 한글과 영문 조합으로 유효한 닉네임
            "special@name, true"    // 특수문자 포함 (유효하지 않음)
    })
    @DisplayName("닉네임 유효성 검사 테스트")
    void testIsInvalidNickname(String nickname, boolean expectedResult) {
        boolean isValid = userService.isInvalidNickname(nickname);

        assertThat(isValid).isEqualTo(expectedResult);
    }


}
