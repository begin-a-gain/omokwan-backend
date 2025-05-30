package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchDay;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchDayRepository extends JpaRepository<MatchDay, Long> {
    List<MatchDay> findAllByMatchId(Long matchId);

}