package begin_a_gain.omokwang.match.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.auth.models.UserPrincipal;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.domain.ParticipantStatus;
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
