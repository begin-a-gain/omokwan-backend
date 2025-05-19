package begin_a_gain.omokwang.match;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import begin_a_gain.omokwang.match.domain.MatchDay;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("대국 테스트")
@ActiveProfiles("local")
@SpringBootTest
@Transactional
public class MatchInfoServiceTest {

    @Autowired
    private MatchRepository repository;

    @Autowired
    private MatchDayRepository matchDayRepository;

    @Autowired
    private UserRepository userRepository;

    MatchInfo savedMatch;
    MatchDay savedDagukDay;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .socialId(123123L)
                .email("test@gmail")
                .nickname("test")
                .platform("kakao")
                .build();

        User savedUser = userRepository.save(user);
        MatchInfo match = MatchInfo.builder()
                .createId(savedUser)
                .name("Test Match")
                .maxParticipants(5)
                .participants(1)
                .category("exercise")
                .isPublic(true)
                .matchCode("1234")
                .password("1234")
                .build();

        MatchDay matchDay = MatchDay.builder().match(match).dayOfWeek(1).build();

        savedMatch = repository.save(match);
        savedDagukDay = matchDayRepository.save(matchDay);
    }

    @Test
    @DisplayName("대국생성테스트")
    void createAndFindMatchById() {

        MatchInfo findMatch = repository.findById(savedMatch.getId())
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        assertThat(savedMatch).isEqualTo(findMatch);
    }

    @Test
    @DisplayName("요일별 대국 조회")
    void

    createAndFindMatchById11() {

        List<MatchInfo> findMatchList = repository.findMatchByUserIdAndDayOfWeek(user.getId(), 1);
        assertThat(findMatchList.get(0)).isEqualTo(savedMatch);
    }


}
