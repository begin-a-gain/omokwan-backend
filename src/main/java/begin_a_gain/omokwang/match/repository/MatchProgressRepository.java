package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchProgress;
import begin_a_gain.omokwang.user.dto.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MatchProgressRepository extends JpaRepository<MatchProgress, Long> {

    @Query("select distinct mp.user from MatchProgress mp where mp.match.id = :matchId")
    List<User> findUsersByMatchId(@Param("matchId") Long matchId);


}