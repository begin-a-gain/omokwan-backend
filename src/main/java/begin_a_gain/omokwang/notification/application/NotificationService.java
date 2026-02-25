package begin_a_gain.omokwang.notification.application;

import begin_a_gain.omokwang.notification.dto.NotificationFilter;
import begin_a_gain.omokwang.notification.dto.NotificationItemResponse;
import begin_a_gain.omokwang.notification.dto.NotificationListResponse;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRecipientRepository notificationRecipientRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId, String rawFilter) {
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
                .map(NotificationItemResponse::from)
                .toList();

        return new NotificationListResponse(items);
    }
}
