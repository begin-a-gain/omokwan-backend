package begin_a_gain.omokwang.match.domain;

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
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchInfo {
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

    @Column(name = "match_code", nullable = false, unique = true)
    private String matchCode;


    @PrePersist
    public void prePersist() {
        this.createDate = LocalDate.now(ZoneId.of("Asia/Seoul")); // 자동으로 현재 날짜 설정
    }

}
