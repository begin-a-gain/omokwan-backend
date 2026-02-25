package begin_a_gain.omokwang.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
        name = "notification_recipient",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notification_recipient", columnNames = {"notification_event_id",
                        "recipient_user_id"})
        },
        indexes = {
                @Index(name = "idx_notification_recipient_user_filter", columnList = "recipient_user_id,is_read,id"),
                @Index(name = "idx_notification_recipient_user_created", columnList = "recipient_user_id,created_at")
        }
)
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_event_id", nullable = false)
    private NotificationEvent notificationEvent;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void markAsRead() {
        if (this.isRead) {
            return;
        }
        this.isRead = true;
        this.readAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
