package begin_a_gain.omokwang.user.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "deletion_survey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletionSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 ID / 유저 PK 등 실제 쓰는 이름에 맞게 변경 가능
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // MySQL JSON 컬럼 + Hibernate 6 JSON 매핑
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reasons", columnDefinition = "json", nullable = false)
    private List<DeletionReason> reasons;

    @Column(name = "other_reason", columnDefinition = "text")
    private String otherReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public DeletionSurvey(Long userId,
                          List<DeletionReason> reasons,
                          String otherReason) {
        this.userId = userId;
        this.reasons = reasons;
        this.otherReason = otherReason;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
