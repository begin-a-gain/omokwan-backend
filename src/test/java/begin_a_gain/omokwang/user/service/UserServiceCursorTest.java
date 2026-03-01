package begin_a_gain.omokwang.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.DeletionSurveyRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceCursorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;
    @Mock
    private DeletionSurveyRepository deletionSurveyRepository;
    @Mock
    private MatchStatusRepository matchStatusRepository;
    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;
    @Mock
    private NotificationEventRepository notificationEventRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("닉네임 검색 결과가 size보다 많으면 hasNext=true와 nextCursor를 반환한다")
    void getUsersByNickname_returnsNextCursorWhenHasNext() {
        var user1 = User.builder().id(1L).nickname("가나").build();
        var user2 = User.builder().id(2L).nickname("가다").build();
        var user3 = User.builder().id(3L).nickname("가라").build();

        when(userRepository.findUsersByNicknameWithCursor(eq("가"), eq(null), eq(null), any()))
                .thenReturn(List.of(user1, user2, user3));

        var response = userService.getUsersByNickname("가", null, 2);

        assertThat(response.users()).hasSize(2);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursor()).isEqualTo("가다::2");
        assertThat(response.users().get(0).nickname()).isEqualTo("가나");
        assertThat(response.users().get(1).nickname()).isEqualTo("가다");
    }

    @Test
    @DisplayName("커서가 있으면 다음 데이터부터 조회한다")
    void getUsersByNickname_usesCursor() {
        var user = User.builder().id(11L).nickname("나다").build();
        when(userRepository.findUsersByNicknameWithCursor(eq("나"), eq("나다"), eq(10L), any()))
                .thenReturn(List.of(user));

        var response = userService.getUsersByNickname("나", "나다::10", 20);

        assertThat(response.users()).hasSize(1);
        assertThat(response.users().get(0).userId()).isEqualTo(11L);
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    @DisplayName("커서 형식이 잘못되면 예외가 발생한다")
    void getUsersByNickname_throwsWhenCursorInvalid() {
        assertThatThrownBy(() -> userService.getUsersByNickname("가", "invalid-cursor", 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid cursor format");
    }
}
