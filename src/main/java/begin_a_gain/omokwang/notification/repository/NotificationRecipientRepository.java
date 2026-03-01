package begin_a_gain.omokwang.notification.repository;

import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @EntityGraph(attributePaths = {"notificationEvent"})
    List<NotificationRecipient> findByRecipientUserIdAndIsReadFalse(Long recipientUserId);

    Optional<NotificationRecipient> findByIdAndRecipientUserId(Long id, Long recipientUserId);

    boolean existsByRecipientUserId(Long recipientUserId);

    boolean existsByRecipientUserIdAndCreatedAtAfter(Long recipientUserId, OffsetDateTime createdAt);

    long countByRecipientUserIdAndIsReadFalse(Long recipientUserId);

    boolean existsByNotificationEvent_IdAndRecipientUserId(Long notificationEventId, Long recipientUserId);

    void deleteByRecipientUserId(Long recipientUserId);

    @Modifying
    @Query("DELETE FROM NotificationRecipient nr WHERE nr.notificationEvent.actorUserId = :actorUserId")
    void deleteByNotificationEventActorUserId(@Param("actorUserId") Long actorUserId);
}
