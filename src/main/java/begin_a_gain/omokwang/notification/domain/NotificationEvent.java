package begin_a_gain.omokwang.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "notification_event",
        indexes = {
                @Index(name = "idx_notification_event_occurred_at", columnList = "occurred_at"),
                @Index(name = "idx_notification_event_match_id", columnList = "match_id"),
                @Index(name = "idx_notification_event_type", columnList = "type")
        }
)
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private NotificationType type;

    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "match_name_snapshot", length = 100)
    private String matchNameSnapshot;

    @Column(name = "actor_nickname_snapshot", length = 50)
    private String actorNicknameSnapshot;

    @Column(name = "prev_host_nickname_snapshot", length = 50)
    private String prevHostNicknameSnapshot;

    @Column(name = "new_host_nickname_snapshot", length = 50)
    private String newHostNicknameSnapshot;


    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        if (this.occurredAt == null) {
            this.occurredAt = nowUtc;
        }
        this.createdAt = nowUtc;
    }
}
