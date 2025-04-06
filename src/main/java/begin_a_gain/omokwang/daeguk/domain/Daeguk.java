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
import jakarta.persistence.PrePersist;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Daeguk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_id", nullable = false)
    private User createId;

    @Column(name = "name")
    private String name;

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDate createDate;

    @Column(name = "max_participants")
    private int maxParticipants;

    @Column(name = "participants")
    private int participants;

    @Column(name = "category")
    private String category;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "password")
    private String password;

    @Column(name = "daeguk_code", nullable = false, unique = true)
    private String daegukCode;


    @PrePersist
    public void prePersist() {
        this.createDate = LocalDate.now(); // 자동으로 현재 날짜 설정
    }

}
