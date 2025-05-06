package begin_a_gain.omokwang.daeguk_detail.domain;

import begin_a_gain.omokwang.daeguk.domain.Daeguk;
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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daeguk_participant", uniqueConstraints = {@UniqueConstraint(columnNames = {"dauguk_id", "user_id"})})
public class DaegukParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daeguk_id", nullable = false)
    private Daeguk daeguk;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDate joinDate;

    @PrePersist
    public void prePersist() {
        this.joinDate = LocalDate.now();
    }

}
