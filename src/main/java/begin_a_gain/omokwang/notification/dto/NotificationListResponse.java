package begin_a_gain.omokwang.notification.dto;

import java.util.List;

public record NotificationListResponse(
        List<NotificationItemResponse> notifications
) {
}
