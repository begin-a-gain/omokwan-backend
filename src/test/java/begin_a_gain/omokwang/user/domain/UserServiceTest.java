package begin_a_gain.omokwang.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UserTest {

    @ParameterizedTest(name = "winningNumber = {0}, userNumber = {1}, bonusNumber = {2}")
    @CsvSource(value = {"1,2,3,4,5,6 : 1,2,3,4,5,6: 8"}, delimiter = ':')
    @DisplayName("닉네임 중복 체크")
    void nickname_duplicate_test(String winningNumbers, String userNumbers, int bonusNumber) {

        assertThat(rank).isEqualTo(Rank.FIRST);
    }

    @ParameterizedTest(name = "winningNumber = {0}, userNumber = {1}, bonusNumber = {2}")
    @CsvSource(value = {"1,2,3,4,5,6 : 1,2,3,4,5,6: 8"}, delimiter = ':')
    @DisplayName("닉네임 유효성 체크")
    void nickname_validation_test(String winningNumbers, String userNumbers, int bonusNumber) {
        Lotto winningLotto = Lotto.createFromString(winningNumbers);
        Lotto userLotto = Lotto.createFromString(userNumbers);
        boolean matchBonus = userNumbers.contains(String.valueOf(bonusNumber));
        Rank rank = Rank.from(userLotto.matchCount(winningLotto), matchBonus);

        assertThat(rank).isEqualTo(Rank.FIRST);
    }
}
