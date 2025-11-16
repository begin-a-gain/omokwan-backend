package begin_a_gain.omokwang.match_detail.repository;


import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.user.dto.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    void deleteByUserId(Long userId);

    @Query("select mp.user from MatchParticipant mp where mp.match.id = :matchId "
            + "order by mp.joinOrder asc")
    List<User> findUsersByMatchId(@Param("matchId") Long matchId);

    @Query("select mp from MatchParticipant mp where mp.match.id = :matchId and mp.user.id = :userId")
    Optional<MatchParticipant> findByMatchIdAndUserId(@Param("matchId") Long matchId,
                                                      @Param("userId") Long userId);

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

}