package begin_a_gain.omokwang.match_detail.domain;

import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.user.dto.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "match_participant", uniqueConstraints = {@UniqueConstraint(columnNames = {"match_id", "user_id"})})
public class MatchParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchInfo match;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private int joinOrder;

    private LocalDate joinDate;

    private LocalDate leaveDate;

    private boolean isHost;

    private LocalDate kickedDate;

    @PrePersist
    public void prePersist() {
        this.joinDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    public void kickNow(Clock clock) {
        this.leaveDate = LocalDate.now(clock);
        this.kickedDate = LocalDate.now(clock);
    }

    public void leaveNow(Clock clock) {
        this.leaveDate = LocalDate.now(clock);
    }
}
