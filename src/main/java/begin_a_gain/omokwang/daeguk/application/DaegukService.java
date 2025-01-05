package begin_a_gain.omokwang.daeguk.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.domain.DaegukRepository;
import begin_a_gain.omokwang.daeguk.dto.CreateDaeGukRequest;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DaegukService {

    private final DaegukRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDaeguk(CreateDaeGukRequest request) {

        long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + currentUserId));

        Daeguk daeguk = mapToDaeguk(request, user);
        Daeguk savedDaeguk = repository.save(daeguk);
        return savedDaeguk.getId();
    }

    private Daeguk mapToDaeguk(CreateDaeGukRequest request, User user) {
        return Daeguk.builder()
                .createId(user)
                .name(request.getName())
                .dayType(request.getDayType())
                .maxParticipants(request.getMaxParticipants())
                .category(request.getCategory())
                .isPublic(request.isPublic())
                .daegukCode(generateDaegukCode())
                .password(request.getPassword())
                .build();
    }

    private String generateDaegukCode() {
        String daegukCode;
        do {
            daegukCode = generateRandomCode(6);
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
}
