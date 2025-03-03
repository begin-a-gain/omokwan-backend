package begin_a_gain.omokwang.daeguk;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.domain.DaegukDay;
import begin_a_gain.omokwang.daeguk.repository.DaegukDayRepository;
import begin_a_gain.omokwang.daeguk.repository.DaegukRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("대국 테스트")
@ActiveProfiles("local")
@SpringBootTest
@Transactional
public class DaegukServiceTest {

    @Autowired
    private DaegukRepository repository;

    @Autowired
    private DaegukDayRepository daegukDayRepository;

    @Autowired
    private UserRepository userRepository;

    Daeguk savedDaeguk;
    DaegukDay savedDagukDay;
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

        Daeguk daeguk = Daeguk.builder()
                .createId(savedUser)
                .name("Test Daeguk")
                .maxParticipants(5)
                .participants(1)
                .category("exercise")
                .isPublic(true)
                .daegukCode("1234")
                .password(1234)
                .build();

        DaegukDay daegukDay = DaegukDay.builder().daeguk(daeguk).dayOfWeek(1).build();

        savedDaeguk = repository.save(daeguk);
        savedDagukDay = daegukDayRepository.save(daegukDay);
    }

    @Test
    @DisplayName("대국생성테스트")
    void createAndFindDaegukById() {

        Daeguk findDaeguk = repository.findById(savedDaeguk.getId())
                .orElseThrow(() -> new IllegalArgumentException("Daeguk not found"));

        assertThat(savedDaeguk).isEqualTo(findDaeguk);
    }

    @Test
    @DisplayName("요일별 대국 조회")
    void

    createAndFindDaegukById11() {

        List<Daeguk> findDaegukList = repository.findDaegukByUserIdAndDayOfWeek(user.getId(), 1);
        assertThat(findDaegukList.get(0)).isEqualTo(savedDaeguk);
    }


}
