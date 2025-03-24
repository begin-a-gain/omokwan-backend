package begin_a_gain.omokwang.daeguk.repository;

import begin_a_gain.omokwang.daeguk.domain.DaegukStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DaegukStatusRepository extends JpaRepository<DaegukStatus, Long> {

    Optional<DaegukStatus> findByDaegukIdAndDaegukDateAndCreateId(Long daegukId, LocalDate daegukDate, Long createId);
}