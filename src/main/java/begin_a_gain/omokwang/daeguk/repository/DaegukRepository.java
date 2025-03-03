package begin_a_gain.omokwang.daeguk.repository;

import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DaegukRepository extends JpaRepository<Daeguk, Long> {
    boolean existsByDaegukCode(String daegukCode);

    @Query("SELECT d FROM Daeguk d " +
            "JOIN DaegukDay dd ON d.id = dd.daeguk.id " +
            "WHERE d.createId.id = :userId AND dd.dayOfWeek = :dayOfWeek")
    List<Daeguk> findDaegukByUserIdAndDayOfWeek(@Param("userId") Long userId,
                                                @Param("dayOfWeek") int dayOfWeek);


}