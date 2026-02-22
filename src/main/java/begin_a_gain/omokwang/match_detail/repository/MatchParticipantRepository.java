package begin_a_gain.omokwang.match_detail.repository;


import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.user.dto.MyPageMatchSummaryProjection;
import begin_a_gain.omokwang.user.dto.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    void deleteByUserId(Long userId);

    long countByUser_IdAndLeaveDateIsNull(Long userId);

    long countByUser_IdAndLeaveDateIsNotNull(Long userId);

    @Query("""
                select mp.user
                from MatchParticipant mp
                where mp.match.id = :matchId
                  and mp.leaveDate IS NULL
                order by mp.joinOrder asc
            """)
    List<User> findUsersByMatchId(@Param("matchId") Long matchId);

    Optional<MatchParticipant> findByMatchIdAndUserId(Long matchId, Long userId);

    @Query("SELECT COALESCE(MAX(mp.joinOrder), 0) FROM MatchParticipant mp WHERE mp.match.id = :matchId")
    int findMaxJoinOrderByMatchId(@Param("matchId") Long matchId);

    Optional<MatchParticipant> findByMatchIdAndIsHostTrue(Long matchId);

    @Modifying
    @Query("""
                UPDATE MatchParticipant mp
                   SET mp.isHost = false
                 WHERE mp.match.id = :matchId
                   AND mp.isHost = true
            """)
    int unsetHost(@Param("matchId") Long matchId);


    @Modifying
    @Query("""
                UPDATE MatchParticipant mp
                   SET mp.isHost = true
                 WHERE mp.match.id = :matchId
                   AND mp.user.id = :userId
            """)
    int setHost(@Param("matchId") Long matchId, @Param("userId") Long userId);

    @Query(value = """
            SELECT
                mi.id AS matchId,
                mi.name AS matchName,
                GREATEST(DATEDIFF(COALESCE(mp.leave_date, CURRENT_DATE), mp.join_date) + 1, 0) AS participantDays,
                COALESCE(stats.combo_count, 0) AS comboCount,
                COALESCE(stats.participant_number, 0) AS participantNumbers,
                GROUP_CONCAT(DISTINCT md.day_of_week ORDER BY md.day_of_week SEPARATOR ',') AS dayOfWeeks
            FROM match_participant mp
            JOIN match_info mi ON mi.id = mp.match_id
            LEFT JOIN match_day md ON md.match_id = mi.id
            LEFT JOIN (
                SELECT
                    ms.match_id AS match_id,
                    ms.create_id AS user_id,
                    SUM(CASE WHEN MOD(ms.streak_count, 5) = 0 THEN 1 ELSE 0 END) AS combo_count,
                    COUNT(*) AS participant_number
                FROM match_status ms
                GROUP BY ms.match_id, ms.create_id
            ) stats ON stats.match_id = mi.id AND stats.user_id = mp.user_id
            WHERE mp.user_id = :userId
              AND mp.leave_date IS NULL
            GROUP BY mi.id, mi.name, mp.join_date, mp.leave_date, stats.combo_count, stats.participant_number
            ORDER BY mi.create_date DESC, mi.id DESC
            """, nativeQuery = true)
    List<MyPageMatchSummaryProjection> findInProgressMatchSummaries(@Param("userId") Long userId);

    @Query(value = """
            SELECT
                mi.id AS matchId,
                mi.name AS matchName,
                GREATEST(DATEDIFF(COALESCE(mp.leave_date, CURRENT_DATE), mp.join_date) + 1, 0) AS participantDays,
                COALESCE(stats.combo_count, 0) AS comboCount,
                COALESCE(stats.participant_number, 0) AS participantNumbers,
                GROUP_CONCAT(DISTINCT md.day_of_week ORDER BY md.day_of_week SEPARATOR ',') AS dayOfWeeks
            FROM match_participant mp
            JOIN match_info mi ON mi.id = mp.match_id
            LEFT JOIN match_day md ON md.match_id = mi.id
            LEFT JOIN (
                SELECT
                    ms.match_id AS match_id,
                    ms.create_id AS user_id,
                    SUM(CASE WHEN MOD(ms.streak_count, 5) = 0 THEN 1 ELSE 0 END) AS combo_count,
                    COUNT(*) AS participant_number
                FROM match_status ms
                GROUP BY ms.match_id, ms.create_id
            ) stats ON stats.match_id = mi.id AND stats.user_id = mp.user_id
            WHERE mp.user_id = :userId
              AND mp.leave_date IS NOT NULL
            GROUP BY mi.id, mi.name, mp.join_date, mp.leave_date, stats.combo_count, stats.participant_number
            ORDER BY MAX(mp.leave_date) DESC, mi.id DESC
            """, nativeQuery = true)
    List<MyPageMatchSummaryProjection> findCompletedMatchSummaries(@Param("userId") Long userId);

}
