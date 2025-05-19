package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchStatusRepository extends JpaRepository<MatchStatus, Long> {

    Optional<MatchStatus> findByMatchIdAndMatchDateAndCreateId(Long matchId, LocalDate matchDate, Long createId);
}