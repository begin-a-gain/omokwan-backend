package begin_a_gain.omokwang.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 배지 상태 응답")
public record NotificationUnreadStatusResponse(
        @Schema(description = "종 배지 표시 여부", example = "true")
        boolean hasBadge
) {
}
