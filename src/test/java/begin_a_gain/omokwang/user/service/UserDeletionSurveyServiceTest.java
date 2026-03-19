package begin_a_gain.omokwang.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.auth.models.UserPrincipal;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.DeletionReason;
import begin_a_gain.omokwang.user.dto.DeletionSurveyRequest;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.DeletionSurveyRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserDeletionSurveyServiceTest {

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("탈퇴 설문 저장 시 reasons와 otherReason을 저장한다")
    void deletionSurvey_savesSurvey() {
        var userId = 1L;
        setAuthenticatedUser(userId);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(User.builder().id(userId).email("test@test.com").build()));

        userService.deletionSurvey(new DeletionSurveyRequest(
                List.of(DeletionReason.MISSING_FEATURES, DeletionReason.OTHER),
                "기타 사유"
        ));

        verify(deletionSurveyRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("탈퇴 설문 저장 시 요청이 없으면 예외가 발생한다")
    void deletionSurvey_throwsWhenRequestIsNull() {
        var userId = 1L;
        setAuthenticatedUser(userId);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(User.builder().id(userId).email("test@test.com").build()));

        assertThatThrownBy(() -> userService.deletionSurvey(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deletion reasons are required");
    }

    private void setAuthenticatedUser(Long userId) {
        var principal = new UserPrincipal(
                userId,
                "test@test.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                null
        );
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
