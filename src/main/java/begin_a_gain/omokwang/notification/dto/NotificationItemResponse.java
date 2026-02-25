package begin_a_gain.omokwang.notification.dto;

import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import java.time.OffsetDateTime;

public record NotificationItemResponse(
        Long notificationId,
        String type,
        boolean isRead,
        OffsetDateTime occurredAt,
        String matchName,
        String actorNickname,
        String prevHostNickname,
        String newHostNickname
) {
    public static NotificationItemResponse from(NotificationRecipient recipient) {
        var event = recipient.getNotificationEvent();
        return new NotificationItemResponse(
                recipient.getId(),
                event.getType().name(),
                recipient.isRead(),
                event.getOccurredAt(),
                event.getMatchNameSnapshot(),
                event.getActorNicknameSnapshot(),
                event.getPrevHostNicknameSnapshot(),
                event.getNewHostNicknameSnapshot()
        );
    }
}
