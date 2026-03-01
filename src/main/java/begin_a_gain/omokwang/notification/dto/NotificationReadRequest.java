package begin_a_gain.omokwang.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 읽음 처리 요청")
public record NotificationReadRequest(
        @Schema(description = "알림 ID(없으면 전체 읽음 처리)", example = "1001")
        Long notificationId
) {
}
