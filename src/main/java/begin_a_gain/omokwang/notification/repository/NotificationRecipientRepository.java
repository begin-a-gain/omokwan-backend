package begin_a_gain.omokwang.notification.repository;

import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

    @EntityGraph(attributePaths = {"notificationEvent"})
    List<NotificationRecipient> findByRecipientUserIdAndNotificationEvent_OccurredAtGreaterThanEqualOrderByCreatedAtDescIdDesc(
            Long recipientUserId,
            OffsetDateTime occurredAt
    );

    @EntityGraph(attributePaths = {"notificationEvent"})
    List<NotificationRecipient> findByRecipientUserIdAndIsReadFalseAndNotificationEvent_OccurredAtGreaterThanEqualOrderByCreatedAtDescIdDesc(
            Long recipientUserId,
            OffsetDateTime occurredAt
    );

    long countByRecipientUserIdAndIsReadFalse(Long recipientUserId);

    boolean existsByNotificationEvent_IdAndRecipientUserId(Long notificationEventId, Long recipientUserId);
}
