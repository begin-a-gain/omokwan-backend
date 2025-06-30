package begin_a_gain.omokwang.match.application;

import static begin_a_gain.omokwang.match.domain.CompletionStatus.COMPLETED;
import static begin_a_gain.omokwang.match.domain.CompletionStatus.NOT_COMPLETED;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.Category;
import begin_a_gain.omokwang.match.domain.CategoryType;
import begin_a_gain.omokwang.match.domain.DayType;
import begin_a_gain.omokwang.match.domain.MatchBoardResponse;
import begin_a_gain.omokwang.match.domain.MatchDay;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.domain.MatchProgress;
import begin_a_gain.omokwang.match.domain.MatchStatus;
import begin_a_gain.omokwang.match.dto.CreateMatchRequest;
import begin_a_gain.omokwang.match.dto.CreateMatchResponse;
import begin_a_gain.omokwang.match.dto.MatchBoardRequest;
import begin_a_gain.omokwang.match.dto.MatchByDayResponse;
import begin_a_gain.omokwang.match.dto.MatchStatusResponse;
import begin_a_gain.omokwang.match.dto.match_board.DateStatus;
import begin_a_gain.omokwang.match.dto.match_board.UserInfo;
import begin_a_gain.omokwang.match.dto.match_board.UserStatus;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchProgressRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private final MatchProgressRepository matchProgressRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_ATTEMPTS = 50;
    private static final int DEFAULT_COMBO_DAYS = 1;
    private static final int DEFAULT_PARTICIPANT = 1;
    private static final int COMBO_MIN_DAYS = 5;

    @Transactional
    public CreateMatchResponse createMatch(CreateMatchRequest request) {

        var socialId = SecurityUtil.getCurrentUserSocialId();
        var user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        var match = mapToMatch(request, user);

        var savedMatch = repository.save(match);
        saveMatchDays(match, request.getDayType());
        saveMatchParticipantProgress(user, match);
        saveMatchParticipant(match, user);
        return CreateMatchResponse.builder().matchId(savedMatch.getId()).build();
    }

    private void saveMatchParticipant(MatchInfo match, User user) {
        var matchParticipant = convertToMatchParticipant(match, user);
        matchParticipantRepository.save(matchParticipant);
    }

    private MatchParticipant convertToMatchParticipant(MatchInfo match, User user) {
        return MatchParticipant.builder()
                .match(match)
                .user(user)
                .build();
    }

    private void saveMatchParticipantProgress(User user, MatchInfo match) {
        var matchProgressRequest = MatchProgress.builder()
                .user(user)
                .match(match)
                .startDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
                .build();
        matchProgressRepository.save(matchProgressRequest);
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
                .participants(DEFAULT_PARTICIPANT)
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

    public List<MatchByDayResponse> findMatchByDay(LocalDate date) {
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
        return (int) ChronoUnit.DAYS.between(createDate, LocalDate.now(ZoneId.of("Asia/Seoul"))) + 1;
    }

    public List<Category> getMatchCategories() {
        return CategoryType.getCategoryList();
    }

    @Transactional
    public MatchStatusResponse matchStatus(LocalDate matchDate, Long matchId) {

        if (isNotToday(matchDate)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        var userId = getUserId();

        Optional<MatchStatus> existingStatus = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(
                matchId, matchDate, userId);

        if (isCompletedMatch(existingStatus)) {
            var comboDays = existingStatus.map(MatchStatus::getComboDays).orElse(0);

            if (comboDays == COMBO_MIN_DAYS) {
                matchStatusRepository.resetRecentCombos(matchId, userId, false);
            }
            matchStatusRepository.deleteById(existingStatus.get().getId());
            return new MatchStatusResponse(false);
        }

        var comboDays = getComboDays(matchId);
        MatchStatus newStatus = MatchStatus.builder()
                .createId(getUserId())
                .matchId(matchId)
                .matchDate(matchDate)
                .completed(true)
                .isCombo(comboDays >= COMBO_MIN_DAYS)
                .comboDays(comboDays)
                .build();
        matchStatusRepository.save(newStatus);

        if (comboDays == COMBO_MIN_DAYS) {
            matchStatusRepository.resetRecentCombos(matchId, userId, true);
        }
        return new MatchStatusResponse(newStatus.isCompleted());
    }

    private boolean isNotToday(LocalDate matchDate) {
        LocalDate todayKST = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return !todayKST.equals(matchDate);
    }

    private boolean isCompletedMatch(Optional<MatchStatus> existingStatus) {
        return existingStatus.isPresent();
    }

    private int getComboDays(Long matchId) {
        var matchDays = getMatchDays(matchId);
        var recentMatchDate = getRecentMatchDate(matchDays);
        System.out.println("test::" + recentMatchDate);
        var recentMatch = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(matchId,
                recentMatchDate, getUserId());

        return recentMatch.map(matchStatus -> matchStatus.getComboDays() + 1).orElse(DEFAULT_COMBO_DAYS);
    }

    private List<Integer> getMatchDays(Long matchId) {
        return matchDayRepository.findAllByMatchId(matchId)
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
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        var todayValue = today.getDayOfWeek().getValue();

        var closestPast = matchDays.stream()
                .filter(day -> day < todayValue)
                .max(Integer::compareTo);

        int targetDay = closestPast.orElseGet(() -> matchDays.stream().max(Integer::compareTo).get());

        int diff = (todayValue >= targetDay) ? todayValue - targetDay : 7 - (targetDay - todayValue);
        return today.minusDays(diff);
    }

    public MatchBoardResponse getBoardForMatch(MatchBoardRequest request) {
        return MatchBoardResponse.builder()
                .users(getUserInfo(request.getMatchId()))
                .dates(getMatchDates(request))
                .nextCursor(getNextCursor(request))
                .build();
    }

    private List<UserInfo> getUserInfo(Long matchId) {
        var users = matchProgressRepository.findUsersByMatchId(matchId);

        return users.stream()
                .map(user -> new UserInfo(user.getId(), user.getNickname()))
                .toList();
    }

    private List<DateStatus> getMatchDates(MatchBoardRequest request) {

        var matchStatuses = getMatchStatuses(request);

        var dateToStatuses = matchStatuses.stream()
                .collect(Collectors.groupingBy(
                        MatchStatus::getMatchDate,
                        Collectors.mapping(status -> new UserStatus(
                                status.getCreateId(),
                                status.isCompleted() ? COMPLETED : NOT_COMPLETED,
                                status.getComboDays(),
                                status.isCombo()
                        ), Collectors.toList())
                ));

        var endDate = request.getDate();
        var pageSize = request.getPageSize();
        return Stream.iterate(endDate, date -> date.minusDays(1))
                .limit(pageSize)
                .map(date -> new DateStatus(
                        date.toString(),
                        dateToStatuses.getOrDefault(date, List.of())
                ))
                .toList();
    }

    private List<MatchStatus> getMatchStatuses(MatchBoardRequest request) {
        var endDate = request.getDate();
        var pageSize = request.getPageSize();
        var startDate = endDate.minusDays(pageSize - 1L);

        return matchStatusRepository.findByMatchIdAndMatchDateBetween(
                request.getMatchId(),
                startDate,
                endDate);
    }

    private LocalDate getNextCursor(MatchBoardRequest request) {
        var startDate = request.getDate();
        return startDate.minusDays(request.getPageSize());
    }

}
