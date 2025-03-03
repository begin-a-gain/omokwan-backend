package begin_a_gain.omokwang.daeguk.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.daeguk.domain.Category;
import begin_a_gain.omokwang.daeguk.domain.CategoryType;
import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.domain.DaegukDay;
import begin_a_gain.omokwang.daeguk.domain.DayType;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukRequest;
import begin_a_gain.omokwang.daeguk.dto.CreateDaegukResponse;
import begin_a_gain.omokwang.daeguk.dto.DaegukByDayResponse;
import begin_a_gain.omokwang.daeguk.repository.DaegukDayRepository;
import begin_a_gain.omokwang.daeguk.repository.DaegukRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DaegukService {

    private final DaegukRepository repository;
    private final UserRepository userRepository;
    private final DaegukDayRepository daegukDayRepository;

    private static final int MAX_ATTEMPTS = 50;

    @Transactional
    public CreateDaegukResponse createDaeguk(CreateDaegukRequest request) {

        long socialId = SecurityUtil.getCurrentUserSocialId();

        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        Daeguk daeguk = mapToDaeguk(request, user);
        Daeguk savedDaeguk = repository.save(daeguk);
        saveDaegukDays(daeguk, request.getDayType());
        return CreateDaegukResponse.builder().daegukId(savedDaeguk.getId()).build();
    }

    private Daeguk mapToDaeguk(CreateDaegukRequest request, User user) {
        if (Objects.nonNull(request.getCategoryCode()) && !CategoryType.isValidCategory(request.getCategoryCode())) {
            throw new IllegalArgumentException("Invalid category code: " + request.getCategoryCode());
        }

        return Daeguk.builder()
                .createId(user)
                .name(request.getName())
                .maxParticipants(request.getMaxParticipants())
                .participants(1)
                .category(request.getCategoryCode())
                .isPublic(request.isPublic())
                .password(request.getPassword())
                .daegukCode(generateDaegukCode())
                .build();
    }

    private void saveDaegukDays(Daeguk daeguk, List<Integer> dayType) {
        List<Integer> extendedDays = DayType.expandDays(dayType);
        for (var day : extendedDays) {
            var daegukInfo = DaegukDay.builder().daeguk(daeguk).dayOfWeek(day).build();
            daegukDayRepository.save(daegukInfo);
        }

    }

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

    public List<DaegukByDayResponse> findDaegukByday(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek().getValue();
        var socialId = SecurityUtil.getCurrentUserSocialId();
        var userId = userRepository.findBySocialId(socialId).map(User::getId).orElse(null);

        var daegukList = repository.findDaegukByUserIdAndDayOfWeek(userId, dayOfWeek);
        return daegukList.stream()
                .map(x -> DaegukByDayResponse.builder()
                        .daegukId(x.getId())
                        .name(x.getName())
                        .ongoingDays(calculateOngoingDays(x.getCreateDate()))
                        .participants(x.getParticipants())
                        .maxParticipants(x.getMaxParticipants())
                        .isPublic(x.isPublic())
                        .build()).toList();
    }

    public int calculateOngoingDays(LocalDate createDate) {
        return (int) ChronoUnit.DAYS.between(createDate, LocalDate.now()) + 1;
    }

    public List<Category> getDaegukCategories() {
        return CategoryType.getCategoryList();
    }
}
