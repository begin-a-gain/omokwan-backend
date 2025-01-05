package begin_a_gain.omokwang.daeguk;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.domain.DaegukRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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
    private UserRepository userRepository;

    @Test
    @DisplayName("대국생성테스트")
    void createAndFindDaegukById() {
        User user = User.builder()
                .socialId(123123L)
                .email("test@gmail")
                .nickname("test")
                .platform("kakao")
                .build();
        
        User savedUser = userRepository.save(user);

        Daeguk daeguk = Daeguk.builder()
                .createId(savedUser)
                .name("Test Daeguk")
                .dayType(List.of(1, 2, 3))
                .maxParticipants(5)
                .category("exercise")
                .isPublic(true)
                .daegukCode("1234")
                .password(1234)
                .build();

        Daeguk savedDaeguk = repository.save(daeguk);
        Daeguk findDaeguk = repository.findById(savedDaeguk.getId())
                .orElseThrow(() -> new IllegalArgumentException("Daeguk not found"));

        assertThat(savedDaeguk).isEqualTo(findDaeguk);
    }
}
