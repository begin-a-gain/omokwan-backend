package begin_a_gain.omokwang.daeguk.domain;

import begin_a_gain.omokwang.user.dto.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "daeguk_pregress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"daeguk_id", "user_id", "progress_date"})})
public class DaegukProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daeguk_id", nullable = false)
    Daeguk daeguk;

    @Column(nullable = false)
    private LocalDate pregressDate;

    private boolean isCompleted;
}
