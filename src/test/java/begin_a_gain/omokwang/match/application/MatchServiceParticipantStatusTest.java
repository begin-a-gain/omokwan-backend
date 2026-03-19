package begin_a_gain.omokwang.match.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.auth.models.UserPrincipal;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchDay;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.domain.ParticipantStatus;
import begin_a_gain.omokwang.match.dto.MatchBoardRequest;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MatchServiceParticipantStatusTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchDayRepository matchDayRepository;
    @Mock
    private MatchStatusRepository matchStatusRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;

    @InjectMocks
    private MatchService matchService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("내 대국 조회 시 leaveDate와 kickedDate가 모두 없으면 ACTIVE 상태를 반환한다")
    void findMatchByDay_returnsActiveParticipantStatus() {
        var userId = 1L;
        var date = LocalDate.of(2026, 3, 1);
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder()
                .id(100L)
                .createId(user)
                .name("오목 대국")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(5)
                .isPublic(true)
                .matchCode("MATCH-CODE")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findMatchByUserIdAndDayOfWeek(userId, date.getDayOfWeek().getValue(), date))
                .thenReturn(List.of(match));
        when(matchParticipantRepository.findUsersByMatchId(100L)).thenReturn(List.of(user));
        when(matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(100L, date, userId)).thenReturn(Optional.empty());
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(user).build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = matchService.findMatchByDay(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipantStatus()).isEqualTo(ParticipantStatus.ACTIVE);
    }

    @Test
    @DisplayName("내 대국 조회 시 leaveDate만 있으면 LEFT 상태를 반환한다")
    void findMatchByDay_returnsLeftParticipantStatus() {
        var userId = 1L;
        var date = LocalDate.of(2026, 3, 1);
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder()
                .id(100L)
                .createId(user)
                .name("오목 대국")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(5)
                .isPublic(true)
                .matchCode("MATCH-CODE")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findMatchByUserIdAndDayOfWeek(userId, date.getDayOfWeek().getValue(), date))
                .thenReturn(List.of(match));
        when(matchParticipantRepository.findUsersByMatchId(100L)).thenReturn(List.of(user));
        when(matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(100L, date, userId)).thenReturn(Optional.empty());
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(
                        MatchParticipant.builder()
                                .match(match)
                                .user(user)
                                .leaveDate(LocalDate.of(2026, 2, 28))
                                .build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = matchService.findMatchByDay(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipantStatus()).isEqualTo(ParticipantStatus.LEFT);
    }

    @Test
    @DisplayName("내 대국 조회 시 kickedDate가 있으면 KICKED 상태를 반환한다")
    void findMatchByDay_returnsKickedParticipantStatus() {
        var userId = 1L;
        var date = LocalDate.of(2026, 3, 1);
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder()
                .id(100L)
                .createId(user)
                .name("오목 대국")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(5)
                .isPublic(true)
                .matchCode("MATCH-CODE")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findMatchByUserIdAndDayOfWeek(userId, date.getDayOfWeek().getValue(), date))
                .thenReturn(List.of(match));
        when(matchParticipantRepository.findUsersByMatchId(100L)).thenReturn(List.of(user));
        when(matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(100L, date, userId)).thenReturn(Optional.empty());
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(
                        MatchParticipant.builder()
                                .match(match)
                                .user(user)
                                .leaveDate(LocalDate.of(2026, 2, 28))
                                .kickedDate(LocalDate.of(2026, 2, 28))
                                .build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = matchService.findMatchByDay(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipantStatus()).isEqualTo(ParticipantStatus.KICKED);
    }

    @Test
    @DisplayName("내 대국 조회 시 참가 정보가 없으면 NOT_FOUND 예외를 던진다")
    void findMatchByDay_throwsNotFoundWhenParticipantMissing() {
        var userId = 1L;
        var date = LocalDate.of(2026, 3, 1);
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder()
                .id(100L)
                .createId(user)
                .name("오목 대국")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(5)
                .isPublic(true)
                .matchCode("MATCH-CODE")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findMatchByUserIdAndDayOfWeek(userId, date.getDayOfWeek().getValue(), date))
                .thenReturn(List.of(match));
        when(matchParticipantRepository.findUsersByMatchId(100L)).thenReturn(List.of(user));
        when(matchStatusRepository.findByMatchIdAndMatchDateAndCreateId(100L, date, userId)).thenReturn(Optional.empty());
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.findMatchByDay(date))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.NOT_FOUND));
    }

    @Test
    @DisplayName("getParticipantStatus는 leaveDate와 kickedDate가 모두 없으면 ACTIVE를 반환한다")
    void getParticipantStatus_returnsActive() {
        var userId = 1L;
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder().id(100L).createId(user).name("오목 대국").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(user).build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = ReflectionTestUtils.invokeMethod(matchService, "getParticipantStatus", 100L);

        assertThat(result).isEqualTo(ParticipantStatus.ACTIVE);
    }

    @Test
    @DisplayName("getParticipantStatus는 leaveDate만 있으면 LEFT를 반환한다")
    void getParticipantStatus_returnsLeft() {
        var userId = 1L;
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder().id(100L).createId(user).name("오목 대국").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(
                        MatchParticipant.builder()
                                .match(match)
                                .user(user)
                                .leaveDate(LocalDate.of(2026, 2, 28))
                                .build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = ReflectionTestUtils.invokeMethod(matchService, "getParticipantStatus", 100L);

        assertThat(result).isEqualTo(ParticipantStatus.LEFT);
    }

    @Test
    @DisplayName("getParticipantStatus는 kickedDate가 있으면 KICKED를 반환한다")
    void getParticipantStatus_returnsKicked() {
        var userId = 1L;
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder().id(100L).createId(user).name("오목 대국").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(
                        MatchParticipant.builder()
                                .match(match)
                                .user(user)
                                .leaveDate(LocalDate.of(2026, 2, 28))
                                .kickedDate(LocalDate.of(2026, 2, 28))
                                .build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(1L);

        var result = ReflectionTestUtils.invokeMethod(matchService, "getParticipantStatus", 100L);

        assertThat(result).isEqualTo(ParticipantStatus.KICKED);
    }

    @Test
    @DisplayName("getParticipantStatus는 참가 정보가 없으면 NOT_FOUND 예외를 던진다")
    void getParticipantStatus_throwsNotFoundWhenParticipantMissing() {
        var userId = 1L;
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(matchService, "getParticipantStatus", 100L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.NOT_FOUND));
    }

    @Test
    @DisplayName("getParticipantStatus는 현재 대국 참여자가 아무도 없으면 DONE을 반환한다")
    void getParticipantStatus_returnsDoneWhenNoActiveParticipants() {
        var userId = 1L;
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder().id(100L).createId(user).name("오목 대국").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(100L, userId))
                .thenReturn(Optional.of(
                        MatchParticipant.builder()
                                .match(match)
                                .user(user)
                                .leaveDate(LocalDate.of(2026, 2, 28))
                                .build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(100L)).thenReturn(0L);

        var result = ReflectionTestUtils.invokeMethod(matchService, "getParticipantStatus", 100L);

        assertThat(result).isEqualTo(ParticipantStatus.DONE);
    }

    @Test
    @DisplayName("대국 보드 조회 시 대국 이름과 최대 참가 인원을 함께 반환한다")
    void getBoardForMatch_includesMatchSummary() {
        var userId = 1L;
        var matchId = 100L;
        var requestDate = LocalDate.of(2026, 3, 16);
        setAuthenticatedUser(userId);

        var user = User.builder().id(userId).email("test@test.com").nickname("test").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(user)
                .name("오목 대국")
                .createDate(LocalDate.of(2026, 3, 1))
                .maxParticipants(8)
                .isPublic(true)
                .matchCode("MATCH-CODE")
                .build();
        var participant = MatchParticipant.builder().match(match).user(user).build();
        var request = MatchBoardRequest.builder()
                .matchId(matchId)
                .date(requestDate)
                .pageSize(1)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findByMatchIdAndUserId(matchId, userId)).thenReturn(Optional.of(participant));
        when(matchParticipantRepository.findUsersByMatchId(matchId)).thenReturn(List.of(user));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(user).isHost(true).build()));
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchDayRepository.findAllByMatchId(matchId))
                .thenReturn(List.of(MatchDay.builder().match(match).dayOfWeek(1).build()));
        when(matchParticipantRepository.countByMatch_IdAndLeaveDateIsNull(matchId)).thenReturn(1L);
        when(matchStatusRepository.findByMatchIdAndMatchDateBetween(
                eq(matchId),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of());
        when(matchStatusRepository.existsByMatchIdAndCreateIdAndCompletedDate(
                eq(matchId),
                eq(userId),
                any(LocalDate.class)
        )).thenReturn(false);

        var result = matchService.getBoardForMatch(request);

        assertThat(result.match()).isNotNull();
        assertThat(result.match().matchName()).isEqualTo("오목 대국");
        assertThat(result.match().maxParticipants()).isEqualTo(8);
    }

    private void setAuthenticatedUser(Long userId) {
        var principal = new UserPrincipal(
                userId,
                "test@test.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                null
        );
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
