package begin_a_gain.omokwang.match_detail.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.match.domain.MatchInfo;
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
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchDetailServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;
    @Mock
    private MatchStatusRepository matchStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationEventRepository notificationEventRepository;
    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;
    @Mock
    private Clock clock;

    @InjectMocks
    private MatchDetailService matchDetailService;

    @Test
    @DisplayName("대국 나가기 시 나를 제외한 참여자들에게 MEMBER_LEFT 알림을 생성한다")
    void leaveMatch_createsMemberLeftNotificationForOtherParticipants() {
        var service = spy(matchDetailService);
        doReturn(1L).when(service).getCurrentUserId();

        var leftUser = User.builder().id(1L).nickname("leave-user").build();
        var anotherUser1 = User.builder().id(2L).nickname("user-2").build();
        var anotherUser2 = User.builder().id(3L).nickname("user-3").build();
        var match = MatchInfo.builder().id(10L).name("오목방").build();
        var participant = MatchParticipant.builder()
                .match(match)
                .user(leftUser)
                .build();

        var fixedInstant = Instant.parse("2026-02-25T10:00:00Z");
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(fixedInstant);
        when(userRepository.findById(1L)).thenReturn(Optional.of(leftUser));
        when(matchParticipantRepository.findByMatchIdAndUserId(10L, 1L)).thenReturn(Optional.of(participant));
        when(matchParticipantRepository.findUsersByMatchId(10L)).thenReturn(List.of(leftUser, anotherUser1, anotherUser2));
        when(notificationEventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.leaveMatch(10L);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(participant.getLeaveDate()).isEqualTo(LocalDate.of(2026, 2, 25));

        var eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventRepository).save(eventCaptor.capture());
        var savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getType()).isEqualTo(NotificationType.MEMBER_LEFT);
        assertThat(savedEvent.getMatchId()).isEqualTo(10L);
        assertThat(savedEvent.getActorUserId()).isEqualTo(1L);
        assertThat(savedEvent.getMatchNameSnapshot()).isEqualTo("오목방");
        assertThat(savedEvent.getActorNicknameSnapshot()).isEqualTo("leave-user");
        assertThat(savedEvent.getOccurredAt()).isNotNull();

        @SuppressWarnings("unchecked")
        var recipientsCaptor = ArgumentCaptor.forClass(List.class);
        verify(notificationRecipientRepository).saveAll(recipientsCaptor.capture());
        List<NotificationRecipient> recipients = recipientsCaptor.getValue();
        assertThat(recipients).hasSize(2);
        assertThat(recipients)
                .extracting(NotificationRecipient::getRecipientUserId)
                .containsExactlyInAnyOrder(2L, 3L);
        assertThat(recipients)
                .extracting(NotificationRecipient::isRead)
                .containsOnly(false);
    }

    @Test
    @DisplayName("대국 나가기 시 다른 참여자가 없으면 알림을 생성하지 않는다")
    void leaveMatch_doesNotCreateNotificationWhenNoOtherParticipants() {
        var service = spy(matchDetailService);
        doReturn(1L).when(service).getCurrentUserId();

        var leftUser = User.builder().id(1L).nickname("leave-user").build();
        var match = MatchInfo.builder().id(10L).name("오목방").build();
        var participant = MatchParticipant.builder()
                .match(match)
                .user(leftUser)
                .build();

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(Instant.parse("2026-02-25T10:00:00Z"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(leftUser));
        when(matchParticipantRepository.findByMatchIdAndUserId(10L, 1L)).thenReturn(Optional.of(participant));
        when(matchParticipantRepository.findUsersByMatchId(10L)).thenReturn(List.of(leftUser));

        var response = service.leaveMatch(10L);

        assertThat(response.getUserId()).isEqualTo(1L);
        verify(notificationEventRepository, never()).save(any(NotificationEvent.class));
        verify(notificationRecipientRepository, never()).saveAll(any());
    }
}
