package begin_a_gain.omokwang.match_detail.repository;


import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.user.dto.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    void deleteByUserId(Long userId);

    @Query("select distinct mp.user from MatchParticipant mp where mp.match.id = :matchId")
    List<User> findUsersByMatchId(@Param("matchId") Long matchId);
}