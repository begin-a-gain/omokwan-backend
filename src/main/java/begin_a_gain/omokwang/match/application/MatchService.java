package begin_a_gain.omokwang.match.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.match.domain.Category;
import begin_a_gain.omokwang.match.domain.CategoryType;
import begin_a_gain.omokwang.match.domain.DayType;
import begin_a_gain.omokwang.match.domain.MatchDay;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.domain.MatchStatus;
import begin_a_gain.omokwang.match.dto.CreateMatchRequest;
import begin_a_gain.omokwang.match.dto.CreateMatchResponse;
import begin_a_gain.omokwang.match.dto.MatchByDayResponse;
import begin_a_gain.omokwang.match.dto.MatchStatusRequest;
import begin_a_gain.omokwang.match.dto.MatchStatusResponse;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository repository;
    private final UserRepository userRepository;
    private final MatchDayRepository matchDayRepository;
    private final MatchStatusRepository matchStatusRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_ATTEMPTS = 50;
    private static final int DEFAULT_COMBO_DAYS = 1;
    private static final int COMBO_MIN_DAYS = 5;

    @Transactional
    public CreateMatchResponse createMatch(CreateMatchRequest request) {

        long socialId = SecurityUtil.getCurrentUserSocialId();

        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        MatchInfo match = mapToMatch(request, user);
        MatchInfo savedMatch = repository.save(match);
        saveMatchDays(match, request.getDayType());
        return CreateMatchResponse.builder().matchId(savedMatch.getId()).build();
    }

    private MatchInfo mapToMatch(CreateMatchRequest request, User user) {
        if (Objects.nonNull(request.getCategoryCode()) && !CategoryType.isValidCategory(request.getCategoryCode())) {
            throw new IllegalArgumentException("Invalid category code: " + request.getCategoryCode());
        }

        String encodedPassword = request.isPublic() ? null : passwordEncoder.encode(request.getPassword());

        return MatchInfo.builder()
                .createId(user)
                .name(request.getName())
                .maxParticipants(request.getMaxParticipants())
                .participants(1)
                .category(request.getCategoryCode())
                .isPublic(request.isPublic())
                .password(encodedPassword)
                .matchCode(generateMatchCode()).build();
    }

    private void saveMatchDays(MatchInfo match, List<Integer> dayType) {
        List<Integer> extendedDays = DayType.expandDays(dayType);
        for (var day : extendedDays) {
            var matchInfo = MatchDay.builder().match(match).dayOfWeek(day).build();
            matchDayRepository.save(matchInfo);
        }

    }

    private String generateMatchCode() {
        String matchCode;
        int attempts = 0;

        do {
            matchCode = generateRandomCode(10);
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                throw new IllegalStateException("고유 대국 코드 생성 실패");
            }
        } while (repository.existsByMatchCode(matchCode));
        return matchCode;
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

    public List<MatchByDayResponse> findMatchByday(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek().getValue();
        var socialId = SecurityUtil.getCurrentUserSocialId();
        var userId = userRepository.findBySocialId(socialId).map(User::getId).orElse(null);

        var matchList = repository.findMatchByUserIdAndDayOfWeek(userId, dayOfWeek);

        return matchList.stream().map(x -> MatchByDayResponse.builder().matchId(x.getId()).name(x.getName())
                .ongoingDays(calculateOngoingDays(x.getCreateDate())).participants(x.getParticipants())
                .maxParticipants(x.getMaxParticipants()).isPublic(x.isPublic())
                .completed(findMatchStatusByday(x.getId(), date, userId)).build()).toList();
    }

    public boolean findMatchStatusByday(Long matchId, LocalDate matchDate, Long userId) {
        Optional<MatchStatus> matchStatus = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(matchId,
                matchDate, userId);
        return matchStatus.map(MatchStatus::isCompleted).orElse(false);
    }

    public int calculateOngoingDays(LocalDate createDate) {
        return (int) ChronoUnit.DAYS.between(createDate, LocalDate.now()) + 1;
    }

    public List<Category> getMatchCategories() {
        return CategoryType.getCategoryList();
    }

    @Transactional
    public MatchStatusResponse matchStatus(LocalDate matchDate, MatchStatusRequest request) {

        var comboDays = getComboDays(request);
        var isCombo = comboDays >= COMBO_MIN_DAYS;

        MatchStatus newStatus = MatchStatus.builder()
                .createId(getUserId())
                .matchId(request.getMatchId())
                .matchDate(matchDate)
                .completed(request.isCompleted())
                .isCombo(isCombo)
                .comboDays(comboDays)
                .build();
        matchStatusRepository.save(newStatus);

        return new MatchStatusResponse(request.isCompleted());
    }

    private int getComboDays(MatchStatusRequest request) {
        var matchDays = getMatchDays(request);
        var recentMatchDate = getRecentMatchDate(matchDays);
        var recentMatch = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(request.getMatchId(),
                recentMatchDate, getUserId());
        return recentMatch.map(matchStatus -> matchStatus.getComboDays() + 1).orElse(DEFAULT_COMBO_DAYS);
    }

    private List<Integer> getMatchDays(MatchStatusRequest request) {
        return matchDayRepository.findById(request.getMatchId())
                .stream()
                .map(MatchDay::getDayOfWeek)
                .toList();
    }

    private Long getUserId() {
        var socialId = SecurityUtil.getCurrentUserSocialId();
        var user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        return user.getId();
    }


    public LocalDate getRecentMatchDate(List<Integer> matchDays) {
        var today = LocalDate.now();
        var todayValue = today.getDayOfWeek().getValue();

        var closestPast = matchDays.stream()
                .filter(day -> day <= todayValue)
                .max(Integer::compareTo);

        int targetDay = closestPast.orElseGet(() -> matchDays.stream().max(Integer::compareTo).get());

        int diff = (todayValue >= targetDay) ? todayValue - targetDay : 7 - (targetDay - todayValue);
        return today.minusDays(diff);
    }

}
