package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchDay;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MatchDayRepository extends JpaRepository<MatchDay, Long> {
    List<MatchDay> findAllByMatchId(Long matchId);

    @Query("select m.dayOfWeek from MatchDay m where m.match.id = :matchId")
    List<Integer> findDayOfWeeksByMatchId(@Param("matchId") Long matchId);

}