package begin_a_gain.omokwang.match.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.dto.UpdateHostRequest;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.notification.domain.NotificationEvent;
import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import begin_a_gain.omokwang.notification.domain.NotificationType;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchDayRepository matchDayRepository;
    @Mock
    private MatchStatusRepository matchStatusRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;
    @Mock
    private NotificationEventRepository notificationEventRepository;
    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    @DisplayName("대국장 변경 시 HOST_CHANGED 알림이 생성된다")
    void updateHost_createsHostChangedNotification() {
        var service = spy(matchService);
        doReturn(1L).when(service).getUserId();

        var previousHost = User.builder().id(1L).nickname("이전방장").build();
        var newHost = User.builder().id(2L).nickname("새방장").build();
        var otherMember = User.builder().id(3L).nickname("멤버").build();

        var hostParticipant = MatchParticipant.builder().user(previousHost).isHost(true).build();
        var match = MatchInfo.builder().id(10L).name("수요오목방").build();

        var request = new UpdateHostRequest();
        ReflectionTestUtils.setField(request, "userId", 2L);

        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(10L)).thenReturn(Optional.of(hostParticipant));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newHost));
        when(matchRepository.findById(10L)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findUsersByMatchId(10L)).thenReturn(List.of(previousHost, newHost, otherMember));
        when(notificationEventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.updateHost(10L, request);

        assertThat(response.getHostId()).isEqualTo(2L);
        verify(matchParticipantRepository).unsetHost(10L);
        verify(matchParticipantRepository).setHost(10L, 2L);

        var eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventRepository).save(eventCaptor.capture());
        var savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(NotificationType.HOST_CHANGED);
        assertThat(savedEvent.getMatchNameSnapshot()).isEqualTo("수요오목방");
        assertThat(savedEvent.getPrevHostNicknameSnapshot()).isEqualTo("이전방장");
        assertThat(savedEvent.getNewHostNicknameSnapshot()).isEqualTo("새방장");

        @SuppressWarnings("unchecked")
        var recipientsCaptor = ArgumentCaptor.forClass(List.class);
        verify(notificationRecipientRepository).saveAll(recipientsCaptor.capture());
        List<NotificationRecipient> recipients = recipientsCaptor.getValue();
        assertThat(recipients)
                .extracting(NotificationRecipient::getRecipientUserId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @DisplayName("대국장 변경 요청 유저가 기존 방장과 같으면 알림을 생성하지 않는다")
    void updateHost_skipsNotificationWhenRequestedHostIsSameAsCurrentHost() {
        var service = spy(matchService);
        doReturn(1L).when(service).getUserId();

        var previousHost = User.builder().id(1L).nickname("이전방장").build();
        var hostParticipant = MatchParticipant.builder().user(previousHost).isHost(true).build();

        var request = new UpdateHostRequest();
        ReflectionTestUtils.setField(request, "userId", 1L);

        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(10L)).thenReturn(Optional.of(hostParticipant));

        var response = service.updateHost(10L, request);

        assertThat(response.getHostId()).isEqualTo(1L);
        verify(matchParticipantRepository, never()).unsetHost(any());
        verify(matchParticipantRepository, never()).setHost(any(), any());
        verify(notificationEventRepository, never()).save(any(NotificationEvent.class));
        verify(notificationRecipientRepository, never()).saveAll(any());
    }
}
