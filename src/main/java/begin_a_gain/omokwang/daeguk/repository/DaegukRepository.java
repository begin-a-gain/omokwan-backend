package begin_a_gain.omokwang.daeguk.repository;

import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DaegukRepository extends JpaRepository<Daeguk, Long> {
    boolean existsByDaegukCode(String daegukCode);

    @Query(
            "SELECT d From Daeguk d JOIN DaegukParticipant p ON d.id=p.id"
    )
    List<Daeguk> findAllByUserAndDayOfWeek(Long userId, String dayOfWeek);

}