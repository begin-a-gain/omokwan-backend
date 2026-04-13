package begin_a_gain.omokwang.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.MyPageMatchSummaryProjection;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.DeletionSurveyRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceMyPageTest {

    private static final Long USER_ID = 10L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;
    @Mock
    private DeletionSurveyRepository deletionSurveyRepository;
    @Mock
    private MatchStatusRepository matchStatusRepository;
    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;
    @Mock
    private NotificationEventRepository notificationEventRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("completedMatches는 participantNumbers가 0인 완료 대국을 제외한다")
    void getMyPage_excludesCompletedMatchesWithZeroParticipantNumbers() {
        var user = User.builder().id(USER_ID).nickname("오목왕").build();
        var hiddenMatch = projection(1L, "안 둔 대국", 0);
        var visibleMatch = projection(2L, "둔 대국", 4);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findInProgressMatchSummaries(USER_ID)).thenReturn(List.of());
        when(matchParticipantRepository.findCompletedMatchSummaries(USER_ID))
                .thenReturn(List.of(hiddenMatch, visibleMatch));
        when(matchParticipantRepository.countByUser_IdAndLeaveDateIsNull(USER_ID)).thenReturn(0L);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);

            var response = userService.getMyPage(USER_ID);

            assertThat(response.getCompletedMatchCount()).isEqualTo(1);
            assertThat(response.getCompletedMatches()).hasSize(1);
            assertThat(response.getCompletedMatches().getFirst().getMatchId()).isEqualTo(2L);
            assertThat(response.getCompletedMatches().getFirst().getParticipantNumbers()).isEqualTo(4);
        }
    }

    @Test
    @DisplayName("완료 대국이 모두 participantNumbers 0이면 count는 0이고 목록은 빈 배열이다")
    void getMyPage_returnsZeroAndEmptyListWhenAllCompletedMatchesHaveZeroParticipantNumbers() {
        var user = User.builder().id(USER_ID).nickname("오목왕").build();
        var hiddenMatch = projection(1L, "안 둔 대국", 0);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(matchParticipantRepository.findInProgressMatchSummaries(USER_ID)).thenReturn(List.of());
        when(matchParticipantRepository.findCompletedMatchSummaries(USER_ID))
                .thenReturn(List.of(hiddenMatch));
        when(matchParticipantRepository.countByUser_IdAndLeaveDateIsNull(USER_ID)).thenReturn(0L);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);

            var response = userService.getMyPage(USER_ID);

            assertThat(response.getCompletedMatchCount()).isZero();
            assertThat(response.getCompletedMatches()).isEmpty();
        }
    }

    private MyPageMatchSummaryProjection projection(Long matchId, String matchName, Integer participantNumbers) {
        return new MyPageMatchSummaryProjection() {
            @Override
            public Long getMatchId() {
                return matchId;
            }

            @Override
            public String getMatchName() {
                return matchName;
            }

            @Override
            public Integer getParticipantDays() {
                return 3;
            }

            @Override
            public Integer getComboCount() {
                return 1;
            }

            @Override
            public Integer getParticipantNumbers() {
                return participantNumbers;
            }

            @Override
            public String getDayOfWeeks() {
                return "1,3,5";
            }
        };
    }
}
