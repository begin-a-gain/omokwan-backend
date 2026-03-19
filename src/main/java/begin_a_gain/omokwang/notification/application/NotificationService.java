package begin_a_gain.omokwang.notification.application;

import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import begin_a_gain.omokwang.notification.dto.NotificationFilter;
import begin_a_gain.omokwang.notification.dto.NotificationItemResponse;
import begin_a_gain.omokwang.notification.dto.NotificationListResponse;
import begin_a_gain.omokwang.notification.dto.NotificationUnreadStatusResponse;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public NotificationListResponse getNotifications(Long userId, String rawFilter) {
        markAsSeen(userId);

        var filter = NotificationFilter.from(rawFilter);
        var cutoffTime = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);

        var recipients = switch (filter) {
            case ALL -> notificationRecipientRepository
                    .findByRecipientUserIdAndNotificationEvent_OccurredAtGreaterThanEqualOrderByCreatedAtDescIdDesc(
                            userId,
                            cutoffTime
                    );
            case UNREAD -> notificationRecipientRepository
                    .findByRecipientUserIdAndIsReadFalseAndNotificationEvent_OccurredAtGreaterThanEqualOrderByCreatedAtDescIdDesc(
                            userId,
                            cutoffTime
                    );
        };

        var items = recipients.stream()
                .map(recipient -> NotificationItemResponse.from(recipient, resolveMatchPublic(recipient)))
                .toList();

        return new NotificationListResponse(items);
    }

    private boolean resolveMatchPublic(NotificationRecipient recipient) {
        var matchId = recipient.getNotificationEvent().getMatchId();
        if (matchId == null) {
            return false;
        }

        return matchRepository.findById(matchId)
                .map(MatchInfo::isPublic)
                .orElse(false);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        if (notificationId == null) {
            var recipients = notificationRecipientRepository.findByRecipientUserIdAndIsReadFalse(userId);
            recipients.forEach(recipient -> recipient.markAsRead());
            notificationRecipientRepository.saveAll(recipients);
            return;
        }

        notificationRecipientRepository.findByIdAndRecipientUserId(notificationId, userId)
                .ifPresent(recipient -> {
                    recipient.markAsRead();
                    notificationRecipientRepository.save(recipient);
                });
    }

    @Transactional
    public void markAsSeen(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setNotificationLastSeenAt(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Transactional(readOnly = true)
    public NotificationUnreadStatusResponse getUnreadStatus(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        var lastSeenAt = user.getNotificationLastSeenAt();

        boolean hasBadge = (lastSeenAt == null)
                ? notificationRecipientRepository.existsByRecipientUserId(userId)
                : notificationRecipientRepository.existsByRecipientUserIdAndCreatedAtAfter(userId, lastSeenAt);

        return new NotificationUnreadStatusResponse(hasBadge);
    }
}
