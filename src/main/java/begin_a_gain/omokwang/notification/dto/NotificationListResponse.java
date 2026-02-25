package begin_a_gain.omokwang.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "NotificationListResponse", description = "알림 목록 응답")
public record NotificationListResponse(
        @Schema(description = "알림 목록")
        List<NotificationItemResponse> notifications
) {
}
