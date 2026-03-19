package begin_a_gain.omokwang.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.notification.domain.NotificationEvent;
import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import begin_a_gain.omokwang.notification.domain.NotificationType;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("notificationId가 없으면 사용자 전체 안읽음 알림을 읽음 처리한다")
    void markAsRead_marksAllUnreadWhenNotificationIdIsNull() {
        var recipient1 = createUnreadRecipient(1L, 10L);
        var recipient2 = createUnreadRecipient(2L, 20L);

        when(notificationRecipientRepository.findByRecipientUserIdAndIsReadFalse(1L))
                .thenReturn(List.of(recipient1, recipient2));

        notificationService.markAsRead(1L, null);

        assertThat(recipient1.isRead()).isTrue();
        assertThat(recipient2.isRead()).isTrue();
        verify(notificationRecipientRepository).findByRecipientUserIdAndIsReadFalse(1L);
        verify(notificationRecipientRepository, never())
                .findByIdAndRecipientUserId(10L, 1L);
    }

    @Test
    @DisplayName("notificationId가 있으면 해당 알림만 읽음 처리한다")
    void markAsRead_marksSingleNotificationWhenProvided() {
        var recipient = createUnreadRecipient(1L, 30L);

        when(notificationRecipientRepository.findByIdAndRecipientUserId(200L, 1L))
                .thenReturn(Optional.of(recipient));

        notificationService.markAsRead(1L, 200L);

        assertThat(recipient.isRead()).isTrue();
        verify(notificationRecipientRepository).findByIdAndRecipientUserId(200L, 1L);
        verify(notificationRecipientRepository, never()).findByRecipientUserIdAndIsReadFalse(1L);
    }

    @Test
    @DisplayName("알림함 확인 처리 시 notification_last_seen_at을 현재 시각으로 갱신한다")
    void markAsSeen_updatesLastSeenAt() {
        var user = User.builder().id(1L).nickname("user").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.markAsSeen(1L);

        assertThat(user.getNotificationLastSeenAt()).isNotNull();
    }

    @Test
    @DisplayName("lastSeenAt이 null이면 알림 존재 여부로 배지 노출을 계산한다")
    void getUnreadStatus_usesExistsWhenLastSeenAtNull() {
        var user = User.builder().id(1L).nickname("user").notificationLastSeenAt(null).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRecipientRepository.existsByRecipientUserId(1L)).thenReturn(true);

        var response = notificationService.getUnreadStatus(1L);

        assertThat(response.hasBadge()).isTrue();
        verify(notificationRecipientRepository).existsByRecipientUserId(1L);
        verify(notificationRecipientRepository, never()).existsByRecipientUserIdAndCreatedAtAfter(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(OffsetDateTime.class)
        );
    }

    @Test
    @DisplayName("lastSeenAt이 있으면 그 이후 생성 알림 존재 여부로 배지를 계산한다")
    void getUnreadStatus_usesCreatedAtAfterWhenLastSeenAtExists() {
        var seenAt = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5);
        var user = User.builder().id(1L).nickname("user").notificationLastSeenAt(seenAt).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRecipientRepository.existsByRecipientUserIdAndCreatedAtAfter(1L, seenAt)).thenReturn(true);

        var response = notificationService.getUnreadStatus(1L);

        assertThat(response.hasBadge()).isTrue();
        verify(notificationRecipientRepository).existsByRecipientUserIdAndCreatedAtAfter(1L, seenAt);
    }

    @Test
    @DisplayName("알림 목록 조회 시 대국 id를 포함해 반환한다")
    void getNotifications_includesMatchId() {
        var user = User.builder().id(1L).nickname("user").build();
        var recipient = createUnreadRecipient(1L, 30L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRecipientRepository
                .findByRecipientUserIdAndNotificationEvent_OccurredAtGreaterThanEqualOrderByCreatedAtDescIdDesc(
                        org.mockito.ArgumentMatchers.eq(1L),
                        org.mockito.ArgumentMatchers.any(OffsetDateTime.class)
                ))
                .thenReturn(List.of(recipient));

        var response = notificationService.getNotifications(1L, "all");

        assertThat(response.notifications()).hasSize(1);
        assertThat(response.notifications().getFirst().matchId()).isEqualTo(30L);
    }

    private NotificationRecipient createUnreadRecipient(Long userId, Long matchId) {
        var event = NotificationEvent.builder()
                .id(100L)
                .type(NotificationType.MATCH_INVITED)
                .matchId(matchId)
                .actorUserId(9L)
                .matchNameSnapshot("오목방")
                .actorNicknameSnapshot("host")
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        return NotificationRecipient.builder()
                .id(200L)
                .notificationEvent(event)
                .recipientUserId(userId)
                .isRead(false)
                .build();
    }
}
