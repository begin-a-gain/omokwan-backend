package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<MatchInfo, Long> {
    boolean existsByMatchCode(String matchCode);

    @Query("SELECT d FROM MatchInfo d " +
            "JOIN MatchDay dd ON d.id = dd.match.id " +
            "WHERE d.createId.id = :userId AND dd.dayOfWeek = :dayOfWeek")
    List<MatchInfo> findMatchByUserIdAndDayOfWeek(@Param("userId") Long userId,
                                                  @Param("dayOfWeek") int dayOfWeek);


}