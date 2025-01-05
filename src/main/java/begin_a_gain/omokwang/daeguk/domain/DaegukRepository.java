package begin_a_gain.omokwang.daeguk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DaegukRepository extends JpaRepository<Daeguk, Long> {
    boolean existsByDaegukCode(String daegukCode);

}
