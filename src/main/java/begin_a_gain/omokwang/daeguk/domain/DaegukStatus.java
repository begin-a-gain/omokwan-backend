package begin_a_gain.omokwang.daeguk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "daeguk_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"daeguk_id", "daeguk_date", "create_id"})})
public class DaegukStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "create_id", nullable = false)
    private Long createId;

    @JoinColumn(name = "daeguk_id", nullable = false)
    private Long daegukId;

    @Column(name = "daeguk_date", nullable = false)
    private LocalDate daegukDate;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @LastModifiedDate
    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;

    public DaegukStatus updateCompletion(boolean isCompleted) {
        return new DaegukStatus(this.id, this.createId, this.daegukId, this.daegukDate, isCompleted, completedDate);
    }
}
