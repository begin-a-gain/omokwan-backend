package begin_a_gain.omokwang.match.repository;

import begin_a_gain.omokwang.match.domain.MatchInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<MatchInfo, Long> {
    boolean existsByMatchCode(String matchCode);

    @Query("""
            SELECT m FROM MatchInfo m
            JOIN MatchDay md ON m.id = md.match.id
            JOIN MatchParticipant mp ON m.id = mp.match.id
            WHERE mp.user.id = :userId AND md.dayOfWeek = :dayOfWeek
            """)
    List<MatchInfo> findMatchByUserIdAndDayOfWeek(@Param("userId") Long userId,
                                                  @Param("dayOfWeek") int dayOfWeek);


}