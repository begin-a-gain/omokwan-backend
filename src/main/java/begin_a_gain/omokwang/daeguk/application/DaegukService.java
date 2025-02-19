package begin_a_gain.omokwang.daeguk.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukRequest;
import begin_a_gain.omokwang.daeguk.repository.DaegukDayRepository;
import begin_a_gain.omokwang.daeguk.repository.DaegukRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DaegukService {

    private final DaegukRepository repository;
    private final UserRepository userRepository;
    private final DaegukDayRepository daegukDayRepository;

    @Transactional
    public Long createDaeguk(CreateDaegukRequest request) {

        long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + currentUserId));

        Daeguk daeguk = mapToDaeguk(request, user);
        Daeguk savedDaeguk = repository.save(daeguk);
        Daeguk savedDaegukDay = daegukDayRepository.save(daeguk);
        return savedDaeguk.getId();
    }

    private Daeguk mapToDaeguk(CreateDaegukRequest request, User user) {
        return Daeguk.builder()
                .createId(user)
                .name(request.getName())
                .maxParticipants(request.getMaxParticipants())
                .category(request.getCategory())
                .isPublic(request.isPublic())
                .password(request.getPassword())
                .daegukCode(generateDaegukCode())
                .build();
    }

    private static final int MAX_ATTEMPTS = 50;

    private String generateDaegukCode() {
        String daegukCode;
        int attempts = 0;

        do {
            daegukCode = generateRandomCode(10);
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                throw new IllegalStateException("고유 대국 코드 생성 실패");
            }
        } while (repository.existsByDaegukCode(daegukCode));
        return daegukCode;
    }


    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public void findDaegukByday(LocalDate date) {
        int day = date.getDayOfWeek().getValue();
        long currentUserId = SecurityUtil.getCurrentUserId();
//        대국 메인 화면의 조회 요일 & 오늘 날짜로 조회를 해야된다

//        repository.findDaegukById(day);
    }
}
