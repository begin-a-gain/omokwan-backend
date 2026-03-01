package begin_a_gain.omokwang.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.DeletionSurveyRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDeleteServiceTest {

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
    @DisplayName("회원 탈퇴 시 user 테이블은 익명화하고 연관 정보는 삭제한다")
    void deleteUser_masksUserAndDeletesRelatedData() {
        var userId = 1L;
        var user = User.builder()
                .id(userId)
                .email("test@test.com")
                .socialId(123L)
                .nickname("tester")
                .platform("kakao")
                .refreshToken("refresh")
                .notificationLastSeenAt(OffsetDateTime.now())
                .deleted(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(matchStatusRepository).deleteByCreateId(userId);
        verify(matchParticipantRepository).deleteByUserId(userId);
        verify(deletionSurveyRepository).deleteByUserId(userId);
        verify(notificationRecipientRepository).deleteByRecipientUserId(userId);
        verify(notificationRecipientRepository).deleteByNotificationEventActorUserId(userId);
        verify(notificationEventRepository).deleteByActorUserId(userId);

        assertThat(user.getDeleted()).isTrue();
        assertThat(user.getSocialId()).isNull();
        assertThat(user.getNickname()).isNull();
        assertThat(user.getPlatform()).isNull();
        assertThat(user.getRefreshToken()).isNull();
        assertThat(user.getNotificationLastSeenAt()).isNull();
        assertThat(user.getEmail()).startsWith("[DELETED]_");
    }
}
