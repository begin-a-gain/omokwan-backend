package begin_a_gain.omokwang.match.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "match_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "match_date", "create_id"})})
public class MatchStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_id", nullable = false)
    private Long createId;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @LastModifiedDate
    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;

    @Column(name = "is_combo")
    private boolean isCombo;

    @Column(name = "streak_count")
    private int streakCount;

    public void updateCompletion(boolean completed) {
        this.completed = completed;
    }
}
