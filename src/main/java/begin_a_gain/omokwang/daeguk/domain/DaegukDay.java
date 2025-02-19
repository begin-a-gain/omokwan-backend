package begin_a_gain.omokwang.daeguk.domain;

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

@Entity
@Table(name = "daeguk_day",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"daeguk_id", "day_of_week"})})
public class DaegukDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daeguk_id", nullable = false)
    Daeguk daeguk;

    @Column(nullable = false)
    private String dayOfWeek;

}
