package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchStatusRepository extends JpaRepository<MatchStatus, Long> {

    Optional<MatchStatus> findByMatchIdAndMatchDateAndCreateId(Long matchId, LocalDate matchDate, Long createId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE match_status
            SET is_combo = :isCombo
            WHERE id IN (
                SELECT id FROM (
                    SELECT id FROM match_status
                    WHERE match_id = :matchId and create_id = :createId
                    ORDER BY match_date DESC
                    LIMIT 5
                ) AS sub
            )
            """, nativeQuery = true)
    void resetRecentCombos(@Param("matchId") Long matchId, @Param("createId") Long createId,
                           @Param("isCombo") boolean isCombo);

    List<MatchStatus> findByMatchIdAndMatchDateBetween(Long matchId, LocalDate startDate,
                                                       LocalDate endDate);

    @Query("""
            SELECT COUNT(ms)
            FROM MatchStatus ms
            WHERE ms.matchId = :matchId
              AND ms.createId = :createId
              AND MOD(ms.streakCount, 5) = 0
            """)
    int comboNumberByMatchIdAndUserId(@Param("matchId") Long matchId,
                                      @Param("createId") Long createId);

    @Query("""
            SELECT COUNT(ms)
            FROM MatchStatus ms
            WHERE ms.matchId = :matchId
              AND ms.createId = :createId
            """)
    int participantNumberByMatchIdAndUserId(@Param("matchId") Long matchId,
                                            @Param("createId") Long createId);

    boolean existsByMatchIdAndCreateIdAndCompletedDate(Long matchId, Long createId, LocalDate completedDate);
}