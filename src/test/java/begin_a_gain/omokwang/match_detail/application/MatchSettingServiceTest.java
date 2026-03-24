package begin_a_gain.omokwang.match_detail.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.dto.MatchSettingUpdateRequest;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchSettingServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchDayRepository matchDayRepository;
    @Mock
    private MatchParticipantRepository matchParticipantRepository;

    @InjectMocks
    private MatchSettingService matchSettingService;

    @Test
    @DisplayName("대국 세팅 수정 시 이름/최대 인원/카테고리/공개 여부가 변경된다")
    void updateSettingMatch_updatesEditableFields() {
        var service = spy(matchSettingService);
        doReturn(1L).when(service).getCurrentUserId();
        var matchId = 10L;
        var owner = User.builder().id(1L).email("test@test.com").nickname("owner").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(owner)
                .name("기존 이름")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(3)
                .category("1")
                .isPublic(false)
                .password("1234")
                .matchCode("ABCD123456")
                .build();

        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "2", true, null);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(owner).isHost(true).build()));
        service.updateSettingMatch(matchId, request);

        assertThat(match.getName()).isEqualTo("수정된 이름");
        assertThat(match.getMaxParticipants()).isEqualTo(5);
        assertThat(match.getCategory()).isEqualTo("2");
        assertThat(match.isPublic()).isTrue();
        assertThat(match.getPassword()).isNull();
        verify(matchRepository, never()).save(any(MatchInfo.class));
    }

    @Test
    @DisplayName("대국 세팅 수정 시 카테고리 코드가 유효하지 않으면 예외가 발생한다")
    void updateSettingMatch_throwsExceptionWhenCategoryInvalid() {
        var service = spy(matchSettingService);
        doReturn(1L).when(service).getCurrentUserId();
        var matchId = 10L;
        var owner = User.builder().id(1L).email("test@test.com").nickname("owner").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(owner)
                .name("기존 이름")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(3)
                .category("1")
                .isPublic(true)
                .matchCode("ABCD123456")
                .build();

        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "99", true, null);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(owner).isHost(true).build()));

        assertThatThrownBy(() -> service.updateSettingMatch(matchId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid category code");
        verify(matchRepository, never()).save(any(MatchInfo.class));
    }

    @Test
    @DisplayName("대국 세팅 수정 시 대국이 없으면 MATCH_NOT_FOUND 예외가 발생한다")
    void updateSettingMatch_throwsWhenMatchNotFound() {
        var service = spy(matchSettingService);
        doReturn(1L).when(service).getCurrentUserId();
        var matchId = 10L;
        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "1", true, null);

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSettingMatch(matchId, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.MATCH_NOT_FOUND));
        verify(matchRepository, never()).save(any(MatchInfo.class));
    }

    @Test
    @DisplayName("공개 대국을 비공개로 바꿀 때 비밀번호가 없으면 예외가 발생한다")
    void updateSettingMatch_throwsWhenSwitchingToPrivateWithoutPassword() {
        var service = spy(matchSettingService);
        doReturn(1L).when(service).getCurrentUserId();
        var matchId = 10L;
        var owner = User.builder().id(1L).email("test@test.com").nickname("owner").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(owner)
                .name("기존 이름")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(3)
                .category("1")
                .isPublic(true)
                .matchCode("ABCD123456")
                .build();

        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "1", false, "");

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(owner).isHost(true).build()));

        assertThatThrownBy(() -> service.updateSettingMatch(matchId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
        verify(matchRepository, never()).save(any(MatchInfo.class));
    }

    @Test
    @DisplayName("공개 대국을 비공개로 바꿀 때 비밀번호를 함께 저장한다")
    void updateSettingMatch_switchesToPrivateWithPassword() {
        var service = spy(matchSettingService);
        doReturn(1L).when(service).getCurrentUserId();
        var matchId = 10L;
        var owner = User.builder().id(1L).email("test@test.com").nickname("owner").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(owner)
                .name("기존 이름")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(3)
                .category("1")
                .isPublic(true)
                .matchCode("ABCD123456")
                .build();

        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "1", false, "9999");

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(owner).isHost(true).build()));
        service.updateSettingMatch(matchId, request);

        assertThat(match.isPublic()).isFalse();
        assertThat(match.getPassword()).isEqualTo("9999");
        verify(matchRepository, never()).save(any(MatchInfo.class));
    }

    @Test
    @DisplayName("호스트가 아니면 대국 세팅을 수정할 수 없다")
    void updateSettingMatch_throwsWhenCurrentUserIsNotHost() {
        var service = spy(matchSettingService);
        doReturn(2L).when(service).getCurrentUserId();
        var matchId = 10L;
        var owner = User.builder().id(1L).email("test@test.com").nickname("owner").build();
        var match = MatchInfo.builder()
                .id(matchId)
                .createId(owner)
                .name("기존 이름")
                .createDate(LocalDate.of(2026, 2, 20))
                .maxParticipants(3)
                .category("1")
                .isPublic(true)
                .matchCode("ABCD123456")
                .build();
        var request = new MatchSettingUpdateRequest("수정된 이름", 5, "2", true, null);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId))
                .thenReturn(Optional.of(MatchParticipant.builder().match(match).user(owner).isHost(true).build()));

        assertThatThrownBy(() -> service.updateSettingMatch(matchId, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }
}
