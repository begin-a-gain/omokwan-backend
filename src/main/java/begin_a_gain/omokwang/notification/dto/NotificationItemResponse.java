package begin_a_gain.omokwang.notification.dto;

import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import begin_a_gain.omokwang.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "NotificationItemResponse", description = "알림 단건 정보")
public record NotificationItemResponse(
        @Schema(description = "알림 ID(recipient id)", example = "1001")
        Long notificationId,
        @Schema(description = "알림 타입", example = "MATCH_JOINED")
        NotificationType type,
        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,
        @Schema(description = "알림 발생 시각(UTC, ISO-8601)", example = "2026-02-25T10:30:12Z")
        OffsetDateTime occurredAt,
        @Schema(description = "대국 이름", example = "수요오목방")
        String matchName,
        @Schema(description = "행위자 닉네임(참여/탈퇴 유저 등)", example = "모기")
        String actorNickname,
        @Schema(description = "이전 방장 닉네임(HOST_CHANGED에서만 사용)", example = "오목이")
        String prevHostNickname,
        @Schema(description = "현재 방장 닉네임(HOST_CHANGED에서만 사용)", example = "모기")
        String newHostNickname
) {
    public static NotificationItemResponse from(NotificationRecipient recipient) {
        var event = recipient.getNotificationEvent();
        return new NotificationItemResponse(
                recipient.getId(),
                event.getType(),
                recipient.isRead(),
                event.getOccurredAt(),
                event.getMatchNameSnapshot(),
                event.getActorNicknameSnapshot(),
                event.getPrevHostNicknameSnapshot(),
                event.getNewHostNicknameSnapshot()
        );
    }
}
