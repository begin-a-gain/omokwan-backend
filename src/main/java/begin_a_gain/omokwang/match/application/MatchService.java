package begin_a_gain.omokwang.match.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.Category;
import begin_a_gain.omokwang.match.domain.CategoryType;
import begin_a_gain.omokwang.match.domain.DayType;
import begin_a_gain.omokwang.match.domain.MatchBoardResponse;
import begin_a_gain.omokwang.match.domain.MatchDay;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.domain.MatchStatus;
import begin_a_gain.omokwang.match.dto.CreateMatchRequest;
import begin_a_gain.omokwang.match.dto.CreateMatchResponse;
import begin_a_gain.omokwang.match.dto.MatchBoardRequest;
import begin_a_gain.omokwang.match.dto.MatchByDayResponse;
import begin_a_gain.omokwang.match.dto.MatchStatusResponse;
import begin_a_gain.omokwang.match.dto.UpdateHostRequest;
import begin_a_gain.omokwang.match.dto.UpdateHostResponse;
import begin_a_gain.omokwang.match.dto.match_board.DateStatus;
import begin_a_gain.omokwang.match.dto.match_board.UserInfo;
import begin_a_gain.omokwang.match.dto.match_board.UserStatus;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchDayRepository matchDayRepository;
    private final MatchStatusRepository matchStatusRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    private static final int MAX_ATTEMPTS = 50;
    private static final int DEFAULT_STREAK_COUNT = 1;
    private static final int DEFAULT_PARTICIPANT = 1;
    private static final int COMBO_MIN_DAYS = 5;

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private static LocalDate todayKST() {
        return LocalDate.now(ZONE_KST);
    }

    @Transactional
    public CreateMatchResponse createMatch(CreateMatchRequest request) {

        var socialId = SecurityUtil.getCurrentUserSocialId();
        var user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
        var match = mapToMatch(request, user);

        var savedMatch = matchRepository.save(match);
        saveMatchDays(match, request.getDayType());
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
                .isHost(true)
                .build();
    }

    private MatchInfo mapToMatch(CreateMatchRequest request, User user) {
        if (Objects.nonNull(request.getCategoryCode()) && !CategoryType.isValidCategory(request.getCategoryCode())) {
            throw new IllegalArgumentException("Invalid category code: " + request.getCategoryCode());
        }

        String encodedPassword = request.getIsPublic() ? null : request.getPassword();

        return MatchInfo.builder()
                .createId(user)
                .name(request.getName())
                .maxParticipants(request.getMaxParticipants())
                .category(request.getCategoryCode())
                .isPublic(request.getIsPublic())
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
        } while (matchRepository.existsByMatchCode(matchCode));
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
        var dayOfWeek = getDayValue(date);
        var socialId = SecurityUtil.getCurrentUserSocialId();
        var userId = userRepository.findBySocialId(socialId).map(User::getId).orElse(null);

        var matchList = matchRepository.findMatchByUserIdAndDayOfWeek(userId, dayOfWeek, date);

        return matchList.stream().map(x -> MatchByDayResponse.builder().matchId(x.getId()).name(x.getName())
                .ongoingDays(calculateOngoingDays(x.getCreateDate()))
                .maxParticipants(x.getMaxParticipants()).isPublic(x.isPublic())
                .participants(matchParticipantRepository.findUsersByMatchId(x.getId()).size())
                .completed(findMatchStatusByDay(x.getId(), date, userId)).build()).toList();
    }

    public boolean findMatchStatusByDay(Long matchId, LocalDate matchDate, Long userId) {
        Optional<MatchStatus> matchStatus = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(matchId,
                matchDate, userId);
        return matchStatus.map(MatchStatus::isCompleted).orElse(false);
    }

    public int calculateOngoingDays(LocalDate createDate) {
        return (int) ChronoUnit.DAYS.between(createDate, todayKST()) + 1;
    }

    public List<Category> getMatchCategories() {
        return CategoryType.getCategoryList();
    }

    @Transactional
    public MatchStatusResponse matchStatus(LocalDate matchDate, Long matchId) {

        if (isNotToday(matchDate)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        validateScheduleDay(matchId);
        var userId = getUserId();
        Optional<MatchStatus> existingStatus = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(
                matchId, matchDate, userId);

        if (isCompletedMatch(existingStatus)) {
            var streakCount = existingStatus.map(MatchStatus::getStreakCount).orElse(0);

            if (streakCount == COMBO_MIN_DAYS) {
                matchStatusRepository.resetRecentCombos(matchId, userId, false);
            }
            matchStatusRepository.deleteById(existingStatus.get().getId());
            return new MatchStatusResponse(false);
        }

        var streakCount = getStreakCount(matchId);
        MatchStatus newStatus = MatchStatus.builder()
                .createId(getUserId())
                .matchId(matchId)
                .matchDate(matchDate)
                .completed(true)
                .isCombo(streakCount >= COMBO_MIN_DAYS)
                .streakCount(streakCount)
                .build();
        matchStatusRepository.save(newStatus);

        if (streakCount == COMBO_MIN_DAYS) {
            matchStatusRepository.resetRecentCombos(matchId, userId, true);
        }
        return new MatchStatusResponse(newStatus.isCompleted());
    }

    private void validateScheduleDay(Long matchId) {
        var scheduleDays = getMatchDays(matchId);
        if (!scheduleDays.contains(getDayValue(todayKST()))) {
            throw new IllegalArgumentException("Today is not part of the schedule.");
        }
    }

    private boolean isNotToday(LocalDate matchDate) {
        LocalDate todayKST = todayKST();
        return !todayKST.equals(matchDate);
    }

    private boolean isCompletedMatch(Optional<MatchStatus> existingStatus) {
        return existingStatus.isPresent();
    }

    private int getStreakCount(Long matchId) {
        var matchDays = getMatchDays(matchId);
        var recentMatchDate = getRecentMatchDate(matchDays);
        var recentMatch = matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(matchId,
                recentMatchDate, getUserId());

        return recentMatch.map(matchStatus -> matchStatus.getStreakCount() + 1).orElse(DEFAULT_STREAK_COUNT);
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
        var today = todayKST();
        var todayValue = getDayValue(today);

        var closestPast = matchDays.stream()
                .filter(day -> day < todayValue)
                .max(Integer::compareTo);

        int targetDay = closestPast.orElseGet(() -> matchDays.stream().max(Integer::compareTo).get());

        int diff = (todayValue >= targetDay) ? todayValue - targetDay : 7 - (targetDay - todayValue);
        return today.minusDays(diff);
    }

    public MatchBoardResponse getBoardForMatch(MatchBoardRequest request) {
        var matchParticipant = matchParticipantRepository.findByMatchIdAndUserId(request.getMatchId(), getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        checkUserStatus(matchParticipant);

        var userInfos = getUserInfo(request.getMatchId());
        validateUserId(userInfos);
        validateSearchDate(request);

        var previousCursor = getPreviousCursor(request);
        var nextCursor = getNextCursor(request);
        return MatchBoardResponse.builder()
                .users(userInfos)
                .dates(getMatchDateInfos(request, userInfos))
                .prevCursor(previousCursor)
                .nextCursor(nextCursor)
                .hasPrev(checkPreviousCheck(previousCursor, request))
                .hasNext(checkHasNext(nextCursor))
                .isTodayMatchCompleted(getMatchCompleted(request))
                .build();
    }

    private void checkUserStatus(MatchParticipant matchParticipant) {
        if (matchParticipant.isKicked()) {
            throw new CustomException(ErrorCode.KICKED_USER);
        }

        if (matchParticipant.isLeft()) {
            throw new CustomException(ErrorCode.LEFT_USER);
        }
    }

    private void validateUserId(List<UserInfo> userInfos) {
        Long currentUserId = getUserId();
        boolean exists = userInfos.stream()
                .anyMatch(joinUserIds -> joinUserIds.userId().equals(currentUserId));

        if (!exists) {
            throw new IllegalArgumentException("Current user is not part of this match");
        }
    }

    private boolean checkPreviousCheck(LocalDate previousCursor, MatchBoardRequest request) {
        var createDate = getCreateDate(request.getMatchId());
        return !previousCursor.isBefore(createDate);
    }

    private boolean checkHasNext(LocalDate nextCursor) {
        return !nextCursor.isAfter(todayKST());
    }

    private boolean getMatchCompleted(MatchBoardRequest request) {
        return matchStatusRepository.existsByMatchIdAndCreateIdAndCompletedDate(request.getMatchId(),
                getUserId()
                , LocalDate.now(ZoneId.of("Asia/Seoul")));
    }

    private List<UserInfo> getUserInfo(Long matchId) {
        var users = matchParticipantRepository.findUsersByMatchId(matchId);
        var hostUser = matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Host not found: " + matchId));
        var hostId = hostUser.getUser().getId();
        var currentUserId = getUserId();

        int currentUseridx = getCurrentUserIdx(users, currentUserId);

        if (currentUseridx > 0) {
            var current = users.remove(currentUseridx);
            users.add(0, current);
        }

        return users.stream()
                .map(user -> new UserInfo(
                        user.getId(),
                        user.getNickname(),
                        user.getId().equals(hostId)))
                .toList();
    }

    private int getCurrentUserIdx(List<User> users, Long currentUserId) {
        return IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(currentUserId))
                .findFirst()
                .orElse(-1);
    }

    private void validateSearchDate(MatchBoardRequest request) {

        if (isAfterToday(request) || isBeforeCreateDate(request)) {
            throw new IllegalArgumentException("Invalid date: " + request.getDate());
        }
    }

    private boolean isAfterToday(MatchBoardRequest request) {
        return request.getDate().isAfter(LocalDate.now(ZoneId.of("Asia/Seoul")));
    }

    private boolean isBeforeCreateDate(MatchBoardRequest request) {
        return request.getDate().isBefore(getCreateDate(request.getMatchId()));
    }

    private List<DateStatus> getMatchDateInfos(MatchBoardRequest request, List<UserInfo> userInfos) {
        var pageSize = request.getPageSize();
        var baseDate = request.getDate();

        var scheduleDays = getMatchDays(request.getMatchId());

        var startDate = getStartDate(request.getMatchId(), pageSize, baseDate, scheduleDays);
        var endDate = getEndDate(pageSize, baseDate, scheduleDays);

        var scheduledDates = getAllScheduleDates(startDate, endDate, scheduleDays);
        if (scheduledDates.isEmpty()) {
            return List.of();
        }

        var matchStatuses = getMatchStatuses(request);
        var dateToStatuses = groupByDate(matchStatuses);

        return getResults(scheduledDates, userInfos, dateToStatuses);
    }

    private Map<LocalDate, List<UserStatus>> groupByDate(List<MatchStatus> matchStatuses) {
        return matchStatuses.stream()
                .collect(Collectors.groupingBy(
                        MatchStatus::getMatchDate,
                        Collectors.mapping(status -> new UserStatus(
                                status.getCreateId(),
                                status.isCompleted(),
                                status.getStreakCount(),
                                status.isCombo()
                        ), Collectors.toList())
                ));
    }

    private List<MatchStatus> getMatchStatuses(MatchBoardRequest request) {
        var pageSize = request.getPageSize();
        var baseDate = request.getDate();
        var scheduleDays = getMatchDays(request.getMatchId());
        var startDate = getStartDate(request.getMatchId(), pageSize, baseDate, scheduleDays);
        var endDate = getEndDate(pageSize, baseDate, scheduleDays);

        var scheduledDates = getAllScheduleDates(startDate, endDate, scheduleDays);
        if (scheduledDates.isEmpty()) {
            return List.of();
        }

        return matchStatusRepository.findByMatchIdAndMatchDateBetween(
                request.getMatchId(),
                startDate,
                endDate);
    }


    private List<LocalDate> getAllScheduleDates(LocalDate startDate, LocalDate endDate, List<Integer> scheduleDays) {
        return Stream.iterate(startDate, d -> !d.isAfter(endDate),
                        d -> d.plusDays(1))
                .filter(d -> scheduleDays.contains(getDayValue(d)))
                .toList();
    }

    private LocalDate getStartDate(Long matchId, int pageSize, LocalDate date, List<Integer> scheduleDays) {
        int count = 0;
        while (count < pageSize) {
            date = date.minusDays(1);
            if (scheduleDays.contains(getDayValue(date))) {
                count++;
            }
        }
        return getMoreRecent(date, getCreateDate(matchId));
    }

    private LocalDate getMoreRecent(LocalDate date, LocalDate createDate) {
        return date.isAfter(createDate) ? date : createDate;
    }

    private LocalDate getCreateDate(Long matchId) {
        var matchInfo = matchRepository.findById(matchId)
                .orElseThrow();
        return matchInfo.getCreateDate();
    }

    private static int getDayValue(LocalDate date) {
        return date.getDayOfWeek().getValue();
    }

    private LocalDate getEndDate(int pageSize, LocalDate date, List<Integer> scheduleDays) {
        int count = 0;
        while (count < pageSize) {
            date = date.plusDays(1);
            if (scheduleDays.contains(getDayValue(date))) {
                count++;
            }
        }
        return date;
    }

    private LocalDate getPreviousCursor(MatchBoardRequest request) {
        var scheduleDays = getMatchDays(request.getMatchId());
        var pageSize = request.getPageSize();
        var date = request.getDate();
        int count = 0;
        while (count < pageSize + 1) {
            date = date.minusDays(1);
            if (scheduleDays.contains(getDayValue(date))) {
                count++;
            }
        }
        return date;
    }

    private LocalDate getNextCursor(MatchBoardRequest request) {
        int count = 0;
        var date = request.getDate();
        var scheduleDays = getMatchDays(request.getMatchId());
        var pageSize = request.getPageSize();
        while (count < pageSize + 1) {
            date = date.plusDays(1);
            if (scheduleDays.contains(getDayValue(date))) {
                count++;
            }
        }
        return date;
    }

    private List<DateStatus> getResults(List<LocalDate> scheduledDates,
                                        List<UserInfo> userInfos,
                                        Map<LocalDate, List<UserStatus>> dateToStatuses) {
        return scheduledDates.stream()
                .map(date -> {
                    var existingByUserId = getExistingByUserId(dateToStatuses, date);

                    List<UserStatus> merged;

                    if (userInfos == null || userInfos.isEmpty()) {
                        merged = existingByUserId.values().stream()
                                .sorted(Comparator.comparing(UserStatus::userId))
                                .toList();
                    } else {
                        merged = userInfos.stream()
                                .map(userInfo -> existingByUserId.getOrDefault(
                                        userInfo.userId(),
                                        defaultStatus(userInfo.userId())
                                ))
                                .collect(Collectors.toList());
                    }

                    return new DateStatus(date.toString(), merged);
                })
                .toList();
    }

    private Map<Long, UserStatus> getExistingByUserId(Map<LocalDate, List<UserStatus>> dateToStatuses, LocalDate date) {
        return dateToStatuses
                .getOrDefault(date, List.of())
                .stream()
                .collect(Collectors.toMap(UserStatus::userId,
                        status -> status,
                        (existing, replacement) -> replacement
                ));
    }

    private static UserStatus defaultStatus(long userId) {
        return new UserStatus(userId, false, 0, false);
    }


    @Transactional
    public UpdateHostResponse updateHost(Long matchId, UpdateHostRequest request) {
        if (!isHost(matchId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        matchParticipantRepository.unsetHost(matchId);
        matchParticipantRepository.setHost(matchId, request.getUserId());
        return UpdateHostResponse.builder()
                .hostId(request.getUserId())
                .build();
    }

    private boolean isHost(Long matchId) {
        var host = matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Host not found: " + matchId));
        var hostId = host.getUser().getId();
        return hostId.equals(getUserId());
    }
}
