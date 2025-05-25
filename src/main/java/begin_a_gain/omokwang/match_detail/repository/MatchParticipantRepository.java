package begin_a_gain.omokwang.match_detail.repository;


import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

}